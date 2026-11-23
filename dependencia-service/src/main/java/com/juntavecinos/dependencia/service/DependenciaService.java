package com.juntavecinos.dependencia.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juntavecinos.dependencia.dto.DependenciaDTO;
import com.juntavecinos.dependencia.dto.DependenciaRequest;
import com.juntavecinos.dependencia.dto.SedeDTO;
import com.juntavecinos.dependencia.exception.ConflictException;
import com.juntavecinos.dependencia.exception.ResourceNotFoundException;
import com.juntavecinos.dependencia.model.Dependencia;
import com.juntavecinos.dependencia.model.Sede;
import com.juntavecinos.dependencia.repository.DependenciaRepository;
import com.juntavecinos.dependencia.repository.SedeRepository;

@Service
public class DependenciaService {

    private final DependenciaRepository dependencias;
    private final SedeRepository sedes;

    public DependenciaService(DependenciaRepository dependencias, SedeRepository sedes) {
        this.dependencias = dependencias;
        this.sedes = sedes;
    }

    /** Solo el administrador ve tambien las dependencias inactivas. */
    @Transactional(readOnly = true)
    public List<DependenciaDTO> listar(boolean incluirInactivas) {
        List<Dependencia> lista = incluirInactivas
                ? dependencias.findAllByOrderByNombreAsc()
                : dependencias.findByActivaTrueOrderByNombreAsc();
        return lista.stream().map(DependenciaDTO::fromModel).toList();
    }

    @Transactional(readOnly = true)
    public DependenciaDTO obtener(Long id) {
        return DependenciaDTO.fromModel(buscar(id));
    }

    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return dependencias.existsById(id);
    }

    @Transactional
    public DependenciaDTO crear(DependenciaRequest request) {
        if (dependencias.existsByNombreIgnoreCase(request.nombre().trim())) {
            throw new ConflictException("Ya existe una dependencia con ese nombre");
        }
        Dependencia dependencia = new Dependencia();
        aplicar(dependencia, request);
        return DependenciaDTO.fromModel(dependencias.save(dependencia));
    }

    @Transactional
    public DependenciaDTO editar(Long id, DependenciaRequest request) {
        Dependencia dependencia = buscar(id);
        if (dependencias.existsByNombreIgnoreCaseAndIdNot(request.nombre().trim(), id)) {
            throw new ConflictException("Ya existe una dependencia con ese nombre");
        }
        aplicar(dependencia, request);
        return DependenciaDTO.fromModel(dependencias.save(dependencia));
    }

    @Transactional
    public DependenciaDTO cambiarActiva(Long id, boolean activa) {
        Dependencia dependencia = buscar(id);
        dependencia.setActiva(activa);
        return DependenciaDTO.fromModel(dependencias.save(dependencia));
    }

    @Transactional(readOnly = true)
    public SedeDTO sede() {
        return SedeDTO.fromModel(buscarSede());
    }

    @Transactional
    public SedeDTO editarSede(SedeDTO request) {
        Sede sede = buscarSede();
        sede.setNombre(request.nombre().trim());
        sede.setDireccion(request.direccion().trim());
        sede.setDescripcion(request.descripcion().trim());
        sede.setReglamento(request.reglamento().trim());
        sede.setInstruccionesPago(request.instruccionesPago().trim());
        return SedeDTO.fromModel(sedes.save(sede));
    }

    private Dependencia buscar(Long id) {
        return dependencias.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dependencia no encontrada"));
    }

    private Sede buscarSede() {
        return sedes.findById(Sede.ID)
                .orElseThrow(() -> new ResourceNotFoundException("No hay información de la sede"));
    }

    private void aplicar(Dependencia d, DependenciaRequest r) {
        d.setNombre(r.nombre().trim());
        d.setDescripcion(r.descripcion() == null ? "" : r.descripcion().trim());
        d.setUbicacion(r.ubicacion() == null ? "" : r.ubicacion().trim());
        d.setCapacidad(r.capacidad());
    }
}
