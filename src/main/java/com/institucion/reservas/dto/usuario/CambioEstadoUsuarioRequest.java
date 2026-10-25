package com.institucion.reservas.dto.usuario;

import com.institucion.reservas.entity.enums.EstadoUsuario;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoUsuarioRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoUsuario estado
) {
}
