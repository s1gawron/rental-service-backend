package com.s1gawron.rentalservice.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    public static final String RESERVATION_COMPLETION_QUEUE = "reservation-completion-queue";

    public static final String RESERVATION_COMPLETION_EXCHANGE = "reservation-completion-de";

    public static final String RESERVATION_COMPLETION_DEAD_LETTER_QUEUE = "reservation-completion-dead-letter-queue";

    private static final String DEAD_LETTER_EXCHANGE_ARGUMENT = "x-dead-letter-exchange";

    private static final String RESERVATION_COMPLETION_DEAD_LETTER_EXCHANGE = RESERVATION_COMPLETION_QUEUE + ".dlx";

    @Bean
    public Queue reservationCompletionQueue() {
        return QueueBuilder.durable(RESERVATION_COMPLETION_QUEUE)
            .withArgument(DEAD_LETTER_EXCHANGE_ARGUMENT, RESERVATION_COMPLETION_DEAD_LETTER_EXCHANGE)
            .build();
    }

    @Bean
    public DirectExchange reservationCompletionExchange() {
        return new DirectExchange(RESERVATION_COMPLETION_EXCHANGE);
    }

    @Bean
    public Binding reservationCompletionBinding() {
        return BindingBuilder.bind(reservationCompletionQueue()).to(reservationCompletionExchange()).with(RESERVATION_COMPLETION_QUEUE);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RESERVATION_COMPLETION_DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(RESERVATION_COMPLETION_DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
    }

}
