package com.juntavecinos.reserva.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.juntavecinos.reserva.dto.*;
import com.juntavecinos.reserva.exception.BadRequestException;
import com.juntavecinos.reserva.exception.ConflictException;
import com.juntavecinos.reserva.exception.ResourceNotFoundException;
import com.juntavecinos.reserva.model.EstadoCatalogo;
import com.juntavecinos.reserva.model.EstadoReserva;
import com.juntavecinos.reserva.model.HistorialReserva;
import com.juntavecinos.reserva.model.Reserva;
import com.juntavecinos.reserva.repository.EstadoCatalogoRepository;
import com.juntavecinos.reserva.repository.HistorialRepository;
import com.juntavecinos.reserva.repository.ReservaRepository;

@Service
public class ReservaService {

    private static final ZoneId ZONA = ZoneId.of("America/Santiago");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final LocalTime APERTURA = LocalTime.of(8, 0);
    private static final LocalTime CIERRE = LocalTime.of(23, 0);
    private static final Set<EstadoReserva> SIN_HORARIO = Set.of(EstadoReserva.RECHAZADA, EstadoReserva.CANCELADA);

    private final ReservaRepository reservas;
    private final HistorialRepository historial;
    private final EstadoCatalogoRepository estados;
    private final DependenciaCliente dependencias;
    private final NotificacionCliente notificaciones;
    private final TransactionTemplate transaccion;

    public ReservaService(ReservaRepository reservas, HistorialRepository historial, EstadoCatalogoRepository estados,
            DependenciaCliente dependencias, NotificacionCliente notificaciones, TransactionTemplate transaccion) {
        this.reservas = reservas;
        this.historial = historial;
        this.estados = estados;
        this.dependencias = dependencias;
        this.notificaciones = notificaciones;
        this.transaccion = transaccion;
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> listar() {
        return reservas.findAllByOrderByFechaAscHoraInicioAsc().stream().map(ReservaDTO::fromModel).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> listarPropias(Long usuarioId) {
        return reservas.findByUsuarioIdOrderByFechaAscHoraInicioAsc(usuarioId).stream()
                .map(ReservaDTO::fromModel).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> listarConfirmadas() {
        return reservas.findByEstadoOrderByFechaAscHoraInicioAsc(EstadoReserva.CONFIRMADA).stream()
                .map(ReservaDTO::paraTesoreria).toList();
    }

    @Transactional(readOnly = true)
    public List<HorarioDTO> disponibilidad(Long dependenciaId, LocalDate fecha) {
        return reservas.findByDependenciaIdAndFechaAndEstadoNotInOrderByHoraInicioAsc(dependenciaId, fecha, SIN_HORARIO)
                .stream().map(r -> new HorarioDTO(r.getHoraInicio(), r.getHoraFin())).toList();
    }

    /** Una reserva ajena responde igual que una inexistente. */
    @Transactional(readOnly = true)
    public ReservaDTO obtener(Long id, Long usuarioId, String rol) {
        Reserva r = buscar(id);
        return switch (rol) {
            case "DIRECTIVA" -> ReservaDTO.fromModel(r);
            case "TESORERIA" -> {
                if (r.getEstado() != EstadoReserva.CONFIRMADA) throw noEncontrada();
                yield ReservaDTO.paraTesoreria(r);
            }
            default -> {
                if (!r.getUsuarioId().equals(usuarioId)) throw noEncontrada();
                yield ReservaDTO.fromModel(r);
            }
        };
    }

    @Transactional(readOnly = true)
    public ReservaDTO obtenerInterno(Long id) {
        return ReservaDTO.fromModel(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<HistorialDTO> historial(Long id) {
        buscar(id);
        return historial.findByReservaIdOrderByIdAsc(id).stream().map(HistorialDTO::fromModel).toList();
    }

    public ReservaDTO crear(Long usuarioId, ReservaRequest request) {
        DependenciaDTO dependencia = validar(request);
        Reserva reserva = transaccion.execute(tx -> {
            Reserva nueva = new Reserva();
            nueva.setUsuarioId(usuarioId);
            aplicar(nueva, request, dependencia);
            reservas.saveAndFlush(nueva);
            ocuparHorario(nueva);
            historial.save(new HistorialReserva(nueva, null, usuarioId));
            return nueva;
        });
        notificaciones.aUsuario(usuarioId, "RESERVA_REGISTRADA", "Reserva registrada",
                descripcion(reserva) + " Queda pendiente de confirmación.", reserva.getId(), null);
        notificaciones.aRol("DIRECTIVA", "RESERVA_REGISTRADA", "Nueva solicitud de reserva",
                descripcion(reserva), reserva.getId(), null);
        return ReservaDTO.fromModel(reserva);
    }

    /** El vecino corrige su propia solicitud cuando la directiva pidio ajustes. */
    public ReservaDTO corregir(Long id, Long usuarioId, ReservaRequest request) {
        DependenciaDTO dependencia = validar(request);
        Reserva reserva = transaccion.execute(tx -> {
            Reserva r = buscar(id);
            if (!r.getUsuarioId().equals(usuarioId)) throw noEncontrada();
            if (r.getEstado() != EstadoReserva.REQUIERE_AJUSTES) {
                throw new ConflictException("Solo se puede corregir una solicitud que requiere ajustes");
            }
            aplicar(r, request, dependencia);
            r.setEstado(EstadoReserva.PENDIENTE);
            r.setObservacion("");
            reservas.saveAndFlush(r);
            reservas.liberarHoras(id);
            ocuparHorario(r);
            historial.save(new HistorialReserva(r, EstadoReserva.REQUIERE_AJUSTES, usuarioId));
            return r;
        });
        notificaciones.aRol("DIRECTIVA", "RESERVA_REGISTRADA", "Solicitud corregida",
                descripcion(reserva), reserva.getId(), null);
        return ReservaDTO.fromModel(reserva);
    }

    public ReservaDTO cambiarEstado(Long id, Long responsableId, CambioEstadoRequest request) {
        EstadoReserva nuevo = request.estado();
        String observacion = request.observacion() == null ? "" : request.observacion().trim();
        if ((nuevo == EstadoReserva.RECHAZADA || nuevo == EstadoReserva.REQUIERE_AJUSTES) && observacion.isEmpty()) {
            throw new BadRequestException("Debe indicar una observación para el vecino");
        }
        Reserva reserva = transaccion.execute(tx -> {
            Reserva r = buscar(id);
            EstadoReserva anterior = r.getEstado();
            if (!anterior.puedePasarA(nuevo)) {
                throw new ConflictException("La reserva no puede pasar de " + etiqueta(anterior) + " a " + etiqueta(nuevo));
            }
            if (nuevo == EstadoReserva.CONFIRMADA && r.getFecha().isBefore(LocalDate.now(ZONA))) {
                throw new BadRequestException("No se puede confirmar una reserva de una fecha pasada");
            }
            r.setEstado(nuevo);
            if (!observacion.isEmpty()) r.setObservacion(observacion);
            r.setFechaActualizacion(LocalDateTime.now());
            reservas.saveAndFlush(r);
            if (nuevo.liberaHorario()) reservas.liberarHoras(id);
            historial.save(new HistorialReserva(r, anterior, responsableId));
            return r;
        });
        String detalle = reserva.getObservacion().isEmpty() ? "" : " Observación: " + reserva.getObservacion();
        notificaciones.aUsuario(reserva.getUsuarioId(), "RESERVA_" + nuevo.name(),
                "Reserva: " + etiqueta(nuevo), descripcion(reserva) + detalle, reserva.getId(), null);
        if (nuevo == EstadoReserva.CONFIRMADA) {
            notificaciones.aRol("TESORERIA", "RESERVA_CONFIRMADA", "Reserva confirmada: pago pendiente",
                    descripcion(reserva), reserva.getId(), null);
        }
        return ReservaDTO.fromModel(reserva);
    }

    @Transactional
    public ReservaDTO observar(Long id, Long responsableId, String observacion) {
        Reserva r = buscar(id);
        r.setObservacion(observacion.trim());
        r.setFechaActualizacion(LocalDateTime.now());
        historial.save(new HistorialReserva(r, r.getEstado(), responsableId));
        return ReservaDTO.fromModel(reservas.saveAndFlush(r));
    }

    @Transactional(readOnly = true)
    public List<EstadoDTO> estados() {
        return estados.findAllByOrderByOrdenAsc().stream().map(EstadoDTO::fromModel).toList();
    }

    @Transactional
    public EstadoDTO editarEstado(String codigo, String etiqueta) {
        EstadoCatalogo estado = estados.findById(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado"));
        estado.setEtiqueta(etiqueta.trim());
        return EstadoDTO.fromModel(estados.save(estado));
    }

    // Reglas

    private DependenciaDTO validar(ReservaRequest r) {
        if (r.fecha().isBefore(LocalDate.now(ZONA))) {
            throw new BadRequestException("La fecha no puede ser anterior a hoy");
        }
        if (r.fecha().isAfter(LocalDate.now(ZONA).plusYears(2))) {
            throw new BadRequestException("La fecha es demasiado lejana");
        }
        if (!esHoraValida(r.horaInicio()) || !esHoraValida(r.horaFin())) {
            throw new BadRequestException("El horario debe ser en horas completas entre 08:00 y 23:00");
        }
        if (!r.horaInicio().isBefore(r.horaFin())) {
            throw new BadRequestException("La hora de término debe ser posterior al inicio");
        }
        DependenciaDTO dependencia = dependencias.obtener(r.dependenciaId());
        if (!dependencia.activa()) {
            throw new BadRequestException("La dependencia no está disponible");
        }
        if (r.personas() > dependencia.capacidad()) {
            throw new BadRequestException("Capacidad máxima: " + dependencia.capacidad() + " personas");
        }
        return dependencia;
    }

    private boolean esHoraValida(LocalTime hora) {
        return hora.getMinute() == 0 && hora.getSecond() == 0 && hora.getNano() == 0
                && !hora.isBefore(APERTURA) && !hora.isAfter(CIERRE);
    }

    private void aplicar(Reserva r, ReservaRequest request, DependenciaDTO dependencia) {
        r.setSolicitante(request.solicitante().trim());
        r.setTelefono(request.telefono());
        r.setDependenciaId(dependencia.id());
        r.setDependencia(dependencia.nombre());
        r.setFecha(request.fecha());
        r.setHoraInicio(request.horaInicio());
        r.setHoraFin(request.horaFin());
        r.setPersonas(request.personas());
        r.setMotivo(request.motivo().trim());
        r.setUsaCocina(request.usaCocina());
        r.setMonto(request.monto());
        r.setFechaActualizacion(LocalDateTime.now());
    }

    private void ocuparHorario(Reserva r) {
        try {
            for (int hora = r.getHoraInicio().getHour(); hora < r.getHoraFin().getHour(); hora++) {
                reservas.ocuparHora(r.getDependenciaId(), r.getFecha(), hora, r.getId());
            }
        } catch (DataIntegrityViolationException | ConcurrencyFailureException e) {
            throw new ConflictException("La dependencia ya está reservada en ese horario");
        }
    }

    private Reserva buscar(Long id) {
        return reservas.findById(id).orElseThrow(this::noEncontrada);
    }

    private ResourceNotFoundException noEncontrada() {
        return new ResourceNotFoundException("Reserva no encontrada");
    }

    private String etiqueta(EstadoReserva estado) {
        return estados.findById(estado.name()).map(EstadoCatalogo::getEtiqueta).orElse(estado.name());
    }

    private String descripcion(Reserva r) {
        return r.getDependencia() + " el " + r.getFecha().format(FECHA) + " de " + r.getHoraInicio() + " a "
                + r.getHoraFin() + ".";
    }
}
