package com.institucion.reservas.service;

import com.institucion.reservas.dto.usuario.CambioEstadoUsuarioRequest;
import com.institucion.reservas.dto.usuario.UsuarioRequest;
import com.institucion.reservas.dto.usuario.UsuarioResponse;
import com.institucion.reservas.entity.Usuario;
import com.institucion.reservas.exception.ConflictException;
import com.institucion.reservas.exception.NotFoundException;
import com.institucion.reservas.mapper.UsuarioMapper;
import com.institucion.reservas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByDocumento(request.documento())) {
            throw new ConflictException(
                    "DATOS_DUPLICADOS",
                    "Ya existe un usuario con el documento indicado"
            );
        }
        if (usuarioRepository.existsByCorreo(request.correo())) {
            throw new ConflictException(
                    "DATOS_DUPLICADOS",
                    "Ya existe un usuario con el correo indicado"
            );
        }

        Usuario usuario = UsuarioMapper.toEntity(request);
        try {
            usuario = usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException(
                    "DATOS_DUPLICADOS",
                    "Ya existe un usuario con el documento o correo indicado"
            );
        }
        return UsuarioMapper.toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(UsuarioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return UsuarioMapper.toResponse(buscar(id));
    }

    @Transactional
    public UsuarioResponse cambiarEstado(Long id, CambioEstadoUsuarioRequest request) {
        Usuario usuario = buscar(id);
        usuario.setEstado(request.estado());
        return UsuarioMapper.toResponse(usuario);
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "USUARIO_NO_ENCONTRADO",
                        "No existe un usuario con id " + id
                ));
    }
}
