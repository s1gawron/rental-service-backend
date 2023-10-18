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
public class ReservationCompletionListener {

    private static final Logger log = LoggerFactory.getLogger(ReservationCompletionListener.class);

    private final ReservationService reservationService;

    private final RabbitTemplate rabbitTemplate;

    public ReservationCompletionListener(final ReservationService reservationService, final RabbitTemplate rabbitTemplate) {
        this.reservationService = reservationService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfiguration.RESERVATION_COMPLETION_QUEUE, concurrency = "1-5")
    public void handle(@Payload final Long reservationId) {
        log.info("Received reservation completion message for reservation#{}", reservationId);
        reservationService.completeReservation(reservationId);
        log.info("Reservation#{} completed successfully", reservationId);
    }

    @RabbitListener(queues = RabbitConfiguration.RESERVATION_COMPLETION_DEAD_LETTER_QUEUE)
    public void handleDeadLetterQueue(@Payload final Message failedMessage) {
        log.info("Requeuing failed message: {}", failedMessage.toString());
        rabbitTemplate.send(RabbitConfiguration.RESERVATION_COMPLETION_EXCHANGE, failedMessage.getMessageProperties().getReceivedRoutingKey(), failedMessage);
    }

}
