package com.s1gawron.rentalservice.scheduled;

import com.google.common.collect.Lists;
import com.s1gawron.rentalservice.reservation.service.ReservationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EndOfDayReservationExpiryStatusCheckJob {

    private static final String EVERY_DAY_AT_MIDNIGHT = "0 0 0 * * *";

    private static final int BATCH_SIZE = 100;

    private final ReservationService reservationService;

    public EndOfDayReservationExpiryStatusCheckJob(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Scheduled(cron = EVERY_DAY_AT_MIDNIGHT)
    public void checkReservationExpiryStatusJob() {
        final List<Long> reservationIds = reservationService.getReservationIds();
        Lists.partition(reservationIds, BATCH_SIZE).forEach(reservationService::checkReservationsExpiryStatus);
    }

}
