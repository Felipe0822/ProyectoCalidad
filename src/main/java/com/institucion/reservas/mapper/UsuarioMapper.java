package com.institucion.reservas.mapper;

import com.institucion.reservas.dto.reserva.UsuarioResumenResponse;
import com.institucion.reservas.dto.usuario.UsuarioRequest;
import com.institucion.reservas.dto.usuario.UsuarioResponse;
import com.institucion.reservas.entity.Usuario;
import com.institucion.reservas.entity.enums.EstadoUsuario;

public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toEntity(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setDocumento(request.documento());
        usuario.setCorreo(request.correo());
        usuario.setEstado(EstadoUsuario.ACTIVO);
        return usuario;
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getDocumento(),
                usuario.getCorreo(),
                usuario.getEstado()
        );
    }

    public static UsuarioResumenResponse toResumen(Usuario usuario) {
        return new UsuarioResumenResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getDocumento()
        );
    }
}
