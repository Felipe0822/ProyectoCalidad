package com.institucion.reservas.dto.usuario;

import com.institucion.reservas.entity.enums.EstadoUsuario;

public record UsuarioResponse(
        Long id,
        String nombre,
        String documento,
        String correo,
        EstadoUsuario estado
) {
}
