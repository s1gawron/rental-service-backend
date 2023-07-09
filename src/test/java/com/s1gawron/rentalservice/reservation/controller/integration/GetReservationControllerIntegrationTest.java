package com.s1gawron.rentalservice.reservation.controller.integration;

import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationListingDTO;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetReservationControllerIntegrationTest extends AbstractReservationControllerIntegrationTest {

    @Test
    void shouldGetUserReservations() throws Exception {
        performMakeReservationRequests();

        final RequestBuilder request = MockMvcRequestBuilders.get(RESERVATION_ENDPOINT + "get/all")
            .header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ReservationListingDTO resultObject = objectMapper.readValue(resultJson, ReservationListingDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(2, resultObject.count());

        for (final ReservationDetailsDTO reservationDetailsDTO : resultObject.reservations()) {
            assertEquals(1, reservationDetailsDTO.tools().size());
        }
    }

    @Test
    void shouldReturnForbiddenResponseWhenUserIsWorkerWhenGettingUserReservations() throws Exception {
        performMakeReservationRequests();

        final String endpoint = RESERVATION_ENDPOINT + "get/all";
        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint).header("Authorization", getAuthorizationToken(WORKER_EMAIL));

        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    void shouldGetReservationDetails() throws Exception {
        performMakeReservationRequests();

        final RequestBuilder request = MockMvcRequestBuilders.get(RESERVATION_ENDPOINT + "get/id/" + currentReservationId)
            .header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ReservationDetailsDTO resultObject = objectMapper.readValue(resultJson, ReservationDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(currentReservationId, resultObject.reservationId());
        assertEquals(LocalDate.now(), resultObject.dateFrom());
        assertEquals(LocalDate.now().plusDays(2L), resultObject.dateTo());
        assertEquals("Hammer", resultObject.additionalComment());
        assertEquals(1, resultObject.tools().size());
        assertEquals(currentToolId, resultObject.tools().get(0).toolId());
        assertEquals("Hammer", resultObject.tools().get(0).name());
    }

    @Test
    void shouldReturnForbiddenResponseWhenUserIsWorkerWhenGettingReservationDetails() throws Exception {
        performMakeReservationRequests();

        final String endpoint = RESERVATION_ENDPOINT + "get/id/" + currentReservationId;
        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint).header("Authorization", getAuthorizationToken(WORKER_EMAIL));

        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenResponseWhenUserIsUnauthenticated() throws Exception {
        performMakeReservationRequests();

        final String endpoint = RESERVATION_ENDPOINT + "get/id/" + currentReservationId;
        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint);

        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundResponseWhenReservationDoesNotBelongToUserWhenGettingReservationDetails() throws Exception {
        performMakeReservationRequests();

        final ReservationNotFoundException expectedException = ReservationNotFoundException.create(currentReservationId);
        final String endpoint = RESERVATION_ENDPOINT + "get/id/" + currentReservationId;
        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint).header("Authorization", getAuthorizationToken(DIFFERENT_CUSTOMER_EMAIL));

        mockMvc.perform(request)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

}
