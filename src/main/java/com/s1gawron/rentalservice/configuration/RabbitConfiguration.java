package com.s1gawron.rentalservice.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    public static final String RESERVATION_EXPIRY_QUEUE = "reservation-expiry-queue";

    public static final String RESERVATION_EXPIRY_EXCHANGE = "reservation-expiry-de";

    public static final String RESERVATION_EXPIRY_DEAD_LETTER_QUEUE = "reservation-expiry-dead-letter-queue";

    private static final String DEAD_LETTER_EXCHANGE_ARGUMENT = "x-dead-letter-exchange";

    private static final String RESERVATION_EXPIRY_DEAD_LETTER_EXCHANGE = RESERVATION_EXPIRY_QUEUE + ".dlx";

    @Bean
    public Queue reservationExpiryQueue() {
        return QueueBuilder.durable(RESERVATION_EXPIRY_QUEUE)
            .withArgument(DEAD_LETTER_EXCHANGE_ARGUMENT, RESERVATION_EXPIRY_DEAD_LETTER_EXCHANGE)
            .build();
    }

    @Bean
    public DirectExchange reservationExpiryExchange() {
        return new DirectExchange(RESERVATION_EXPIRY_EXCHANGE);
    }

    @Bean
    public Binding reservationExpiryBinding() {
        return BindingBuilder.bind(reservationExpiryQueue()).to(reservationExpiryExchange()).with(RESERVATION_EXPIRY_QUEUE);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RESERVATION_EXPIRY_DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(RESERVATION_EXPIRY_DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
    }

}
