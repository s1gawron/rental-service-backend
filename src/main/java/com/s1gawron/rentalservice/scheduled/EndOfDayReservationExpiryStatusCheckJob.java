package com.s1gawron.rentalservice.scheduled;

import com.s1gawron.rentalservice.configuration.RabbitConfiguration;
import com.s1gawron.rentalservice.reservation.service.ReservationService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EndOfDayReservationExpiryStatusCheckJob {

    private static final String EVERY_DAY_AT_MIDNIGHT = "0 0 0 * * *";

    private final ReservationService reservationService;

    private final RabbitTemplate rabbitTemplate;

    public EndOfDayReservationExpiryStatusCheckJob(final ReservationService reservationService, final RabbitTemplate rabbitTemplate) {
        this.reservationService = reservationService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(cron = EVERY_DAY_AT_MIDNIGHT)
    public void checkReservationExpiryStatusJob() {
        final LocalDateTime now = LocalDateTime.now();

        reservationService.getReservationIdsForDateToOlderThan(now)
            .forEach(id -> rabbitTemplate.convertAndSend(RabbitConfiguration.RESERVATION_EXPIRY_EXCHANGE, RabbitConfiguration.RESERVATION_EXPIRY_QUEUE, id));
    }

}
