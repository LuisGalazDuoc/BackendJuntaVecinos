package com.juntavecinos.auth.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juntavecinos.auth.dto.CrearUsuarioRequest;
import com.juntavecinos.auth.dto.EditarUsuarioRequest;
import com.juntavecinos.auth.dto.UsuarioDTO;
import com.juntavecinos.auth.exception.BadRequestException;
import com.juntavecinos.auth.exception.ConflictException;
import com.juntavecinos.auth.exception.ResourceNotFoundException;
import com.juntavecinos.auth.model.Rol;
import com.juntavecinos.auth.model.Usuario;
import com.juntavecinos.auth.repository.RolRepository;
import com.juntavecinos.auth.repository.UsuarioRepository;

/** Administracion de cuentas. Las cuentas se desactivan, no se eliminan. */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final HashService hashService;

    public UsuarioService(UsuarioRepository usuarios, RolRepository roles, HashService hashService) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.hashService = hashService;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listar(Long adminId) {
        exigirAdminVigente(adminId);
        return usuarios.findAllByOrderByNombreAsc().stream().map(UsuarioDTO::fromModel).toList();
    }

    @Transactional
    public UsuarioDTO crear(Long adminId, CrearUsuarioRequest request) {
        exigirAdminVigente(adminId);
        String email = AuthService.normalizar(request.email());
        if (usuarios.existsByEmail(email)) {
            throw new ConflictException("Ya existe una cuenta con ese correo");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre().trim());
        usuario.setEmail(email);
        usuario.setTelefono(request.telefono());
        usuario.setPassword(hashService.sha1(request.password()));
        usuario.setRol(rol(request.rol()));
        return UsuarioDTO.fromModel(usuarios.save(usuario));
    }

    @Transactional
    public UsuarioDTO editar(Long adminId, Long id, EditarUsuarioRequest request) {
        roles.bloquearAdmin();
        exigirAdminVigente(adminId);
        Usuario usuario = buscar(id);
        String email = AuthService.normalizar(request.email());
        if (usuarios.existsByEmailAndIdNot(email, id)) {
            throw new ConflictException("Ya existe una cuenta con ese correo");
        }
        if (adminId.equals(id) && !Rol.ADMIN.equals(request.rol())) {
            throw new BadRequestException("No puede quitarse su propio perfil de administrador");
        }
        usuario.setNombre(request.nombre().trim());
        usuario.setEmail(email);
        usuario.setTelefono(request.telefono());
        usuario.setRol(rol(request.rol()));
        usuarios.saveAndFlush(usuario);
        exigirAlgunAdmin();
        return UsuarioDTO.fromModel(usuario);
    }

    @Transactional
    public UsuarioDTO cambiarActivo(Long adminId, Long id, boolean activo) {
        roles.bloquearAdmin();
        exigirAdminVigente(adminId);
        if (adminId.equals(id) && !activo) {
            throw new BadRequestException("No puede desactivar su propia cuenta");
        }
        Usuario usuario = buscar(id);
        usuario.setActivo(activo);
        usuarios.saveAndFlush(usuario);
        exigirAlgunAdmin();
        return UsuarioDTO.fromModel(usuario);
    }

    @Transactional
    public void cambiarContrasena(Long adminId, Long id, String password) {
        exigirAdminVigente(adminId);
        buscar(id).setPassword(hashService.sha1(password));
    }

    private Usuario buscar(Long id) {
        return usuarios.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private Rol rol(String codigo) {
        return roles.findByCodigo(codigo).orElseThrow(() -> new BadRequestException("El perfil no existe"));
    }

    /** El token puede ser anterior a una desactivacion: se confirma contra la base de datos. */
    private void exigirAdminVigente(Long adminId) {
        usuarios.findById(adminId)
                .filter(u -> u.isActivo() && Rol.ADMIN.equals(u.getRol().getCodigo()))
                .orElseThrow(() -> new AccessDeniedException("Administrador no vigente"));
    }

    private void exigirAlgunAdmin() {
        if (usuarios.countByRolCodigoAndActivoTrue(Rol.ADMIN) == 0) {
            throw new BadRequestException("Debe quedar al menos un administrador activo");
        }
    }
}
