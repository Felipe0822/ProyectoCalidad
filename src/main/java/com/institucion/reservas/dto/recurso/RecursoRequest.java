package com.institucion.reservas.dto.recurso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RecursoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String descripcion,

        @NotBlank(message = "El tipo es obligatorio")
        @Size(max = 50, message = "El tipo no puede superar 50 caracteres")
        String tipo,

        @NotNull(message = "La capacidad es obligatoria")
        @Positive(message = "La capacidad debe ser mayor que cero")
        Integer capacidad
) {
}
