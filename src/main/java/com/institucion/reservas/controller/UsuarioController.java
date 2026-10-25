package com.institucion.reservas.controller;

import com.institucion.reservas.dto.usuario.CambioEstadoUsuarioRequest;
import com.institucion.reservas.dto.usuario.UsuarioRequest;
import com.institucion.reservas.dto.usuario.UsuarioResponse;
import com.institucion.reservas.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.crear(request);
        return ResponseEntity
                .created(URI.create("/usuarios/" + response.id()))
                .body(response);
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(
            @PathVariable @Positive(message = "El id debe ser mayor que cero") Long id
    ) {
        return usuarioService.obtener(id);
    }

    @PatchMapping("/{id}/estado")
    public UsuarioResponse cambiarEstado(
            @PathVariable @Positive(message = "El id debe ser mayor que cero") Long id,
            @Valid @RequestBody CambioEstadoUsuarioRequest request
    ) {
        return usuarioService.cambiarEstado(id, request);
    }
}
