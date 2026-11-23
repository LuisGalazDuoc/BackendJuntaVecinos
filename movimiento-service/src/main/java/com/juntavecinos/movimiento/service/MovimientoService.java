package com.juntavecinos.movimiento.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.juntavecinos.movimiento.dto.*;
import com.juntavecinos.movimiento.exception.BadRequestException;
import com.juntavecinos.movimiento.exception.ConflictException;
import com.juntavecinos.movimiento.exception.ResourceNotFoundException;
import com.juntavecinos.movimiento.model.Categoria;
import com.juntavecinos.movimiento.model.EstadoMovimiento;
import com.juntavecinos.movimiento.model.Movimiento;
import com.juntavecinos.movimiento.model.TipoMovimiento;
import com.juntavecinos.movimiento.model.Visibilidad;
import com.juntavecinos.movimiento.repository.CategoriaRepository;
import com.juntavecinos.movimiento.repository.MovimientoRepository;
import com.juntavecinos.movimiento.repository.VisibilidadRepository;

@Service
public class MovimientoService {

    private static final ZoneId ZONA = ZoneId.of("America/Santiago");

    private final MovimientoRepository movimientos;
    private final CategoriaRepository categorias;
    private final VisibilidadRepository visibilidades;
    private final ReservaCliente reservas;
    private final NotificacionCliente notificaciones;
    private final TransactionTemplate transaccion;

    public MovimientoService(MovimientoRepository movimientos, CategoriaRepository categorias,
            VisibilidadRepository visibilidades, ReservaCliente reservas, NotificacionCliente notificaciones,
            TransactionTemplate transaccion) {
        this.movimientos = movimientos;
        this.categorias = categorias;
        this.visibilidades = visibilidades;
        this.reservas = reservas;
        this.notificaciones = notificaciones;
        this.transaccion = transaccion;
    }

    @Transactional(readOnly = true)
    public List<MovimientoDTO> listar() {
        return movimientos.listarVigentes().stream().map(MovimientoDTO::fromModel).toList();
    }

    @Transactional(readOnly = true)
    public MovimientoDTO obtener(Long id) {
        return MovimientoDTO.fromModel(buscar(id));
    }

    @Transactional(readOnly = true)
    public ResumenDTO resumen() {
        return resumir(movimientos.listarVigentes());
    }

    @Transactional(readOnly = true)
    public FinanzasPublicasDTO publico() {
        Visibilidad v = visibilidad();
        List<Movimiento> vigentes = movimientos.listarVigentes();
        return new FinanzasPublicasDTO(v.isMostrarResumen(), v.isMostrarMovimientos(),
                v.isMostrarResumen() ? resumir(vigentes) : null,
                v.isMostrarMovimientos() ? vigentes.stream().map(MovimientoPublicoDTO::fromModel).toList() : List.of());
    }

    /** Los totales se calculan desde los movimientos vigentes del periodo. */
    @Transactional(readOnly = true)
    public InformeDTO informe(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new BadRequestException("La fecha inicial no puede ser posterior a la final");
        }
        List<Movimiento> lista = movimientos.listarVigentesEntre(desde, hasta);
        List<TotalCategoriaDTO> totales = lista.stream()
                .collect(Collectors.groupingBy(m -> m.getCategoria().getId()))
                .values().stream()
                .map(grupo -> new TotalCategoriaDTO(grupo.get(0).getTipo(), grupo.get(0).getCategoria().getNombre(),
                        sumar(grupo), grupo.size()))
                .sorted(Comparator.comparing(TotalCategoriaDTO::tipo)
                        .thenComparing(TotalCategoriaDTO::total, Comparator.reverseOrder()))
                .toList();
        return new InformeDTO(desde, hasta, resumir(lista), totales,
                lista.stream().map(MovimientoDTO::fromModel).toList());
    }

    public MovimientoDTO registrar(Long usuarioId, MovimientoRequest request) {
        if (request.fecha().isAfter(LocalDate.now(ZONA))) {
            throw new BadRequestException("La fecha no puede ser futura");
        }
        ReservaDTO reserva = request.reservaId() == null ? null : validarPago(request);
        MovimientoDTO creado;
        try {
            creado = transaccion.execute(tx -> guardar(usuarioId, request));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("La reserva ya tiene un pago registrado");
        }
        if (reserva != null) {
            notificaciones.aUsuario(reserva.usuarioId(), "PAGO_REGISTRADO", "Pago registrado",
                    "Se registró el pago informado de su reserva de " + reserva.dependencia() + ".",
                    reserva.id(), creado.id());
        }
        notificaciones.aRol("DIRECTIVA", "MOVIMIENTO_REGISTRADO",
                (creado.tipo() == TipoMovimiento.INGRESO ? "Ingreso" : "Gasto") + " registrado",
                creado.categoria() + ": $" + String.format("%,d", creado.monto()).replace(',', '.'), null, creado.id());
        return creado;
    }

    /** Se anula en vez de eliminar, para conservar la trazabilidad. */
    @Transactional
    public void anular(Long id) {
        Movimiento movimiento = buscar(id);
        if (movimiento.getEstado() == EstadoMovimiento.ANULADO) {
            throw new ConflictException("El movimiento ya está anulado");
        }
        movimiento.setEstado(EstadoMovimiento.ANULADO);
        movimientos.save(movimiento);
    }

    @Transactional(readOnly = true)
    public VisibilidadDTO obtenerVisibilidad() {
        return VisibilidadDTO.fromModel(visibilidad());
    }

    @Transactional
    public VisibilidadDTO editarVisibilidad(VisibilidadDTO request) {
        Visibilidad v = visibilidad();
        v.setMostrarResumen(request.mostrarResumen());
        v.setMostrarMovimientos(request.mostrarMovimientos());
        return VisibilidadDTO.fromModel(visibilidades.save(v));
    }

    private ReservaDTO validarPago(MovimientoRequest request) {
        if (request.tipo() != TipoMovimiento.INGRESO) {
            throw new BadRequestException("El pago de una reserva debe registrarse como ingreso");
        }
        ReservaDTO reserva = reservas.obtener(request.reservaId());
        if (!"CONFIRMADA".equals(reserva.estado())) {
            throw new BadRequestException("Solo se registran pagos de reservas confirmadas");
        }
        if (movimientos.existsByReservaIdAndEstado(request.reservaId(), EstadoMovimiento.VIGENTE)) {
            throw new ConflictException("La reserva ya tiene un pago registrado");
        }
        return reserva;
    }

    private MovimientoDTO guardar(Long usuarioId, MovimientoRequest request) {
        Categoria categoria = categorias.findById(request.categoriaId())
                .filter(Categoria::isActiva)
                .orElseThrow(() -> new BadRequestException("La categoría no está disponible"));
        if (categoria.getTipo() != request.tipo()) {
            throw new BadRequestException("La categoría no corresponde al tipo de movimiento");
        }
        Movimiento m = new Movimiento();
        m.setTipo(request.tipo());
        m.setCategoria(categoria);
        m.setDescripcion(request.descripcion().trim());
        m.setMonto(request.monto());
        m.setFecha(request.fecha());
        m.setReservaId(request.reservaId());
        m.setMedioPago(request.medioPago());
        m.setReferencia(request.referencia());
        m.setUsuarioRegistroId(usuarioId);
        return MovimientoDTO.fromModel(movimientos.saveAndFlush(m));
    }

    private ResumenDTO resumir(List<Movimiento> lista) {
        long ingresos = sumar(lista.stream().filter(m -> m.getTipo() == TipoMovimiento.INGRESO).toList());
        long gastos = sumar(lista.stream().filter(m -> m.getTipo() == TipoMovimiento.GASTO).toList());
        return new ResumenDTO(ingresos, gastos, ingresos - gastos);
    }

    private long sumar(List<Movimiento> lista) {
        return lista.stream().mapToLong(Movimiento::getMonto).sum();
    }

    private Movimiento buscar(Long id) {
        return movimientos.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado"));
    }

    private Visibilidad visibilidad() {
        return visibilidades.findById(Visibilidad.ID)
                .orElseThrow(() -> new ResourceNotFoundException("No hay configuración de visibilidad"));
    }
}
