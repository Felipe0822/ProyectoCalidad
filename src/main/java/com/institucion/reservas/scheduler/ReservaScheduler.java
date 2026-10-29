package com.institucion.reservas.scheduler;

import com.institucion.reservas.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.scheduling.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Slf4j
public class ReservaScheduler {

    private final ReservaService reservaService;

    @Scheduled(fixedRateString = "${app.scheduling.finalizacion-ms:30000}")
    public void finalizarVencidas() {
        int actualizadas = reservaService.finalizarVencidas();
        if (actualizadas > 0) {
            log.info("Reservas finalizadas automáticamente: {}", actualizadas);
        }
    }
}
