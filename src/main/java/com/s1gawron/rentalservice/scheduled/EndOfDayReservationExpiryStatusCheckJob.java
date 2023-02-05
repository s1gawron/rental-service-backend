package com.s1gawron.rentalservice.scheduled;

import com.s1gawron.rentalservice.reservation.service.ReservationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EndOfDayReservationExpiryStatusCheckJob {

    private static final String EVERY_DAY_AT_MIDNIGHT = "0 0 0 * * *";

    private static final int BATCH_SIZE = 500;

    private final ReservationService reservationService;

    public EndOfDayReservationExpiryStatusCheckJob(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Scheduled(cron = EVERY_DAY_AT_MIDNIGHT)
    public void checkReservationExpiryStatusJob() {
        partitionList(reservationService.getReservationIds()).forEach(reservationService::checkReservationsExpiryStatus);
    }

    public Collection<List<Long>> partitionList(final List<Long> listToPartition) {
        return listToPartition.stream()
            .collect(Collectors.groupingBy(s -> s / BATCH_SIZE))
            .values();
    }

}
