package com.s1gawron.rentalservice.reservation.controller.integration;

import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CancelReservationControllerIntegrationTest extends AbstractReservationControllerIntegrationTest {

    private static final String CANCEL_RESERVATION_ENDPOINT = "/api/customer/reservation/cancel/";

    @Test
    void shouldCancelReservation() throws Exception {
        performMakeReservationRequests();

        final RequestBuilder request = MockMvcRequestBuilders.post(CANCEL_RESERVATION_ENDPOINT + currentReservationId)
            .header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ReservationDetailsDTO resultObject = objectMapper.readValue(resultJson, ReservationDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(1, resultObject.tools().size());
        assertTrue(getReservationDetails(currentReservationId).isCanceled());
        assertEquals(currentToolId, resultObject.tools().get(0).toolId());
        assertEquals("Hammer", resultObject.tools().get(0).name());
        assertEquals(BigDecimal.valueOf(10.99), resultObject.reservationFinalPrice());
        assertTrue(toolService.getToolById(currentToolId).isAvailable());
    }

    @Test
    void shouldReturnForbiddenResponseWhenUserIsNotAllowedToCancelReservation() throws Exception {
        performMakeReservationRequests();

        final String endpoint = CANCEL_RESERVATION_ENDPOINT + currentReservationId;
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).header("Authorization", getAuthorizationToken(WORKER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.FORBIDDEN, NO_ACCESS_FOR_USER_ROLE_EXCEPTION.getMessage(), endpoint,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnNotFoundResponseWhenReservationWasNotFoundWhileCancelingReservation() throws Exception {
        performMakeReservationRequests();

        final ReservationNotFoundException expectedException = ReservationNotFoundException.create(currentReservationId);
        final String endpoint = CANCEL_RESERVATION_ENDPOINT + currentReservationId;
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).header("Authorization", getAuthorizationToken(DIFFERENT_CUSTOMER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), endpoint,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

}
