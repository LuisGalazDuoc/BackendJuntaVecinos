package com.juntavecinos.auth.service;

import java.util.Date;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juntavecinos.auth.dto.LoginRequest;
import com.juntavecinos.auth.dto.LoginResponse;
import com.juntavecinos.auth.dto.RegistroRequest;
import com.juntavecinos.auth.dto.UsuarioDTO;
import com.juntavecinos.auth.exception.ConflictException;
import com.juntavecinos.auth.exception.UnauthorizedException;
import com.juntavecinos.auth.model.Rol;
import com.juntavecinos.auth.model.Usuario;
import com.juntavecinos.auth.repository.RolRepository;
import com.juntavecinos.auth.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final HashService hashService;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarios, RolRepository roles, HashService hashService, JwtService jwtService) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.hashService = hashService;
        this.jwtService = jwtService;
    }

    /** El mismo mensaje para correo inexistente, clave incorrecta o cuenta inactiva. */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String hash = hashService.sha1(request.password());
        Usuario usuario = usuarios.findByEmail(normalizar(request.email()))
                .filter(u -> u.isActivo() && hash.equals(u.getPassword()))
                .orElseThrow(() -> new UnauthorizedException("Correo o contraseña incorrectos"));
        Date expiracion = jwtService.nuevaExpiracion();
        String token = jwtService.generateToken(usuario, expiracion);
        return new LoginResponse("ok", token, expiracion.getTime(), UsuarioDTO.fromModel(usuario));
    }

    @Transactional
    public UsuarioDTO registrar(RegistroRequest request) {
        String email = normalizar(request.email());
        if (usuarios.existsByEmail(email)) {
            throw new ConflictException("Ya existe una cuenta con ese correo");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre().trim());
        usuario.setEmail(email);
        usuario.setTelefono(request.telefono());
        usuario.setPassword(hashService.sha1(request.password()));
        usuario.setRol(roles.findByCodigo(Rol.VECINO).orElseThrow());
        return UsuarioDTO.fromModel(usuarios.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO actual(Long id) {
        return usuarios.findById(id).filter(Usuario::isActivo).map(UsuarioDTO::fromModel)
                .orElseThrow(() -> new UnauthorizedException("La cuenta no está activa"));
    }

    static String normalizar(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
