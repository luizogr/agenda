package com.ufvjm.agenda.controllers;

import com.ufvjm.agenda.dto.LoginRequestDTO;
import com.ufvjm.agenda.dto.RegisterRequestDTO;
import com.ufvjm.agenda.dto.ResponseDTO;
import com.ufvjm.agenda.entities.Usuario;
import com.ufvjm.agenda.infra.security.TokenService;
import com.ufvjm.agenda.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private final PasswordEncoder encoder;
    @Autowired
    private final TokenService tokenService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder encoder, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    /*@PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body){
        Usuario usuario = this.usuarioRepository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if (encoder.matches(body.senha(), usuario.getSenha())) {
            String token = this.tokenService.generateToken(usuario);
            return ResponseEntity.ok(new ResponseDTO(usuario.getNome(), token));
        }
        return ResponseEntity.badRequest().build();
    }*/

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body){
        Optional<Usuario> usuario = this.usuarioRepository.findByEmail(body.email());

        if (usuario.isEmpty()){
            Usuario newUsuario = new Usuario();
            newUsuario.setEmail(body.email());
            newUsuario.setSenha(encoder.encode(body.senha()));
            newUsuario.setNome(body.nome());

            this.usuarioRepository.save(newUsuario);

            String token = this.tokenService.generateToken(newUsuario);
            return ResponseEntity.ok(new ResponseDTO(newUsuario.getNome(), token));

        }
        return ResponseEntity.badRequest().build();
    }
}
