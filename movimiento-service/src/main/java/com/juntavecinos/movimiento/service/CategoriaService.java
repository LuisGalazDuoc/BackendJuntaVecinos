package com.juntavecinos.movimiento.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juntavecinos.movimiento.dto.CategoriaDTO;
import com.juntavecinos.movimiento.dto.CategoriaRequest;
import com.juntavecinos.movimiento.exception.BadRequestException;
import com.juntavecinos.movimiento.exception.ConflictException;
import com.juntavecinos.movimiento.exception.ResourceNotFoundException;
import com.juntavecinos.movimiento.model.Categoria;
import com.juntavecinos.movimiento.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categorias;

    public CategoriaService(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    @Transactional(readOnly = true)
    public List<CategoriaDTO> listar(boolean incluirInactivas) {
        List<Categoria> lista = incluirInactivas ? categorias.findAllByOrderByTipoDescIdAsc()
                : categorias.findByActivaTrueOrderByTipoDescIdAsc();
        return lista.stream().map(CategoriaDTO::fromModel).toList();
    }

    @Transactional
    public CategoriaDTO crear(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setTipo(request.tipo());
        aplicar(categoria, request);
        return CategoriaDTO.fromModel(categorias.save(categoria));
    }

    /** El tipo no cambia, porque alteraria los movimientos ya registrados. */
    @Transactional
    public CategoriaDTO editar(Long id, CategoriaRequest request) {
        Categoria categoria = categorias.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        if (categoria.getTipo() != request.tipo()) {
            throw new BadRequestException("No se puede cambiar el tipo de una categoría");
        }
        aplicar(categoria, request);
        return CategoriaDTO.fromModel(categorias.save(categoria));
    }

    private void aplicar(Categoria c, CategoriaRequest r) {
        String nombre = r.nombre().trim();
        categorias.findByNombreIgnoreCaseAndTipo(nombre, r.tipo())
                .filter(otra -> !otra.getId().equals(c.getId()))
                .ifPresent(otra -> {
                    throw new ConflictException("Ya existe una categoría con ese nombre");
                });
        c.setNombre(nombre);
        c.setDescripcion(r.descripcion() == null ? "" : r.descripcion().trim());
        c.setActiva(r.activa());
    }
}
