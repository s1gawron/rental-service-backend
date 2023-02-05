package com.s1gawron.rentalservice.scheduled;

import com.s1gawron.rentalservice.reservation.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EndOfDayReservationExpiryStatusCheckJobTest {

    private ReservationService reservationServiceMock;

    private List<Long> listToPartition;

    private EndOfDayReservationExpiryStatusCheckJob endOfDayReservationExpiryStatusCheckJob;

    @BeforeEach
    void setUp() {
        reservationServiceMock = Mockito.mock(ReservationService.class);
        listToPartition = new ArrayList<>();

        for (long i = 0; i < 1450; i++) {
            listToPartition.add(i);
        }

        endOfDayReservationExpiryStatusCheckJob = new EndOfDayReservationExpiryStatusCheckJob(reservationServiceMock);
    }

    @Test
    void shouldSplitReservationIdList() {
        Mockito.when(reservationServiceMock.getReservationIds()).thenReturn(listToPartition);

        final Collection<List<Long>> partitionedReservationIdList = endOfDayReservationExpiryStatusCheckJob.partitionList(listToPartition);
        final ArrayList<List<Long>> result = new ArrayList<>(partitionedReservationIdList);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(500, result.get(0).size());
        assertEquals(500, result.get(1).size());
        assertEquals(450, result.get(2).size());
    }

}