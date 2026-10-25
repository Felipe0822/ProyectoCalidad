package com.institucion.reservas.service;

import com.institucion.reservas.dto.recurso.CambioEstadoRecursoRequest;
import com.institucion.reservas.dto.recurso.RecursoRequest;
import com.institucion.reservas.dto.recurso.RecursoResponse;
import com.institucion.reservas.entity.Recurso;
import com.institucion.reservas.exception.NotFoundException;
import com.institucion.reservas.mapper.RecursoMapper;
import com.institucion.reservas.repository.RecursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecursoService {

    private final RecursoRepository recursoRepository;

    @Transactional
    public RecursoResponse crear(RecursoRequest request) {
        Recurso recurso = RecursoMapper.toEntity(request);
        recurso = recursoRepository.saveAndFlush(recurso);
        return RecursoMapper.toResponse(recurso);
    }

    @Transactional(readOnly = true)
    public List<RecursoResponse> listar() {
        return recursoRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(RecursoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecursoResponse obtener(Long id) {
        return RecursoMapper.toResponse(buscar(id));
    }

    @Transactional
    public RecursoResponse actualizar(Long id, RecursoRequest request) {
        Recurso recurso = buscar(id);
        RecursoMapper.updateEntity(recurso, request);
        return RecursoMapper.toResponse(recurso);
    }

    @Transactional
    public RecursoResponse cambiarEstado(Long id, CambioEstadoRecursoRequest request) {
        Recurso recurso = buscar(id);
        recurso.setEstado(request.estado());
        return RecursoMapper.toResponse(recurso);
    }

    private Recurso buscar(Long id) {
        return recursoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "RECURSO_NO_ENCONTRADO",
                        "No existe un recurso con id " + id
                ));
    }
}
