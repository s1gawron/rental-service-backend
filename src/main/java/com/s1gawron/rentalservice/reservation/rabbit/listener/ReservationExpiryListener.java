package com.s1gawron.rentalservice.reservation.rabbit.listener;

import com.s1gawron.rentalservice.configuration.RabbitConfiguration;
import com.s1gawron.rentalservice.reservation.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ReservationExpiryListener {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpiryListener.class);

    private final ReservationService reservationService;

    private final RabbitTemplate rabbitTemplate;

    public ReservationExpiryListener(final ReservationService reservationService, final RabbitTemplate rabbitTemplate) {
        this.reservationService = reservationService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfiguration.RESERVATION_EXPIRY_QUEUE, concurrency = "1-5")
    public void handle(@Payload final Long reservationId) {
        log.info("Received reservation expiry message for reservation#{}", reservationId);
        reservationService.expireReservation(reservationId);
        log.info("Reservation#{} expiry clean job finished successfully", reservationId);
    }

    @RabbitListener(queues = RabbitConfiguration.RESERVATION_EXPIRY_DEAD_LETTER_QUEUE)
    public void handleDeadLetterQueue(@Payload final Message failedMessage) {
        log.info("Requeuing failed message: {}", failedMessage.toString());
        rabbitTemplate.send(RabbitConfiguration.RESERVATION_EXPIRY_EXCHANGE, failedMessage.getMessageProperties().getReceivedRoutingKey(), failedMessage);
    }

}
