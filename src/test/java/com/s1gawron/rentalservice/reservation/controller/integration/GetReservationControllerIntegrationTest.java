package com.s1gawron.rentalservice.reservation.controller.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationListingDTO;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetReservationControllerIntegrationTest extends AbstractReservationControllerIntegrationTest {

    @Test
    @SneakyThrows
    void shouldGetUserReservations() {
        performMakeReservationRequests();

        final RequestBuilder request = MockMvcRequestBuilders.get(RESERVATION_ENDPOINT + "get/all")
            .header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ReservationListingDTO resultObject = objectMapper.readValue(resultJson, ReservationListingDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(2, resultObject.getCount());

        for (final ReservationDetailsDTO reservationDetailsDTO : resultObject.getReservations()) {
            assertEquals(1, reservationDetailsDTO.getTools().size());
        }
    }

    @Test
    @SneakyThrows
    void shouldReturnForbiddenResponseWhenUserIsWorkerWhenGettingUserReservations() {
        performMakeReservationRequests();

        final String endpoint = RESERVATION_ENDPOINT + "get/all";
        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint).header("Authorization", getAuthorizationToken(WORKER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.FORBIDDEN, NO_ACCESS_FOR_USER_ROLE_EXCEPTION.getMessage(), endpoint,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldGetReservationDetails() {
        performMakeReservationRequests();

        final RequestBuilder request = MockMvcRequestBuilders.get(RESERVATION_ENDPOINT + "get/id/" + currentReservationId)
            .header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ReservationDetailsDTO resultObject = objectMapper.readValue(resultJson, ReservationDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(currentReservationId, resultObject.getReservationId());
        assertEquals(LocalDate.now(), resultObject.getDateFrom());
        assertEquals(LocalDate.now().plusDays(2L), resultObject.getDateTo());
        assertEquals("Hammer", resultObject.getAdditionalComment());
        assertEquals(1, resultObject.getTools().size());
        assertEquals(currentToolId, resultObject.getTools().get(0).getToolId());
        assertEquals("Hammer", resultObject.getTools().get(0).getName());
    }

    @Test
    @SneakyThrows
    void shouldReturnForbiddenResponseWhenUserIsWorkerWhenGettingReservationDetails() {
        performMakeReservationRequests();

        final String endpoint = RESERVATION_ENDPOINT + "get/id/" + currentReservationId;
        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint).header("Authorization", getAuthorizationToken(WORKER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.FORBIDDEN, NO_ACCESS_FOR_USER_ROLE_EXCEPTION.getMessage(), endpoint,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnNotFoundResponseWhenReservationDoesNotBelongToUserWhenGettingReservationDetails() {
        performMakeReservationRequests();

        final ReservationNotFoundException expectedException = ReservationNotFoundException.create(currentReservationId);
        final String endpoint = RESERVATION_ENDPOINT + "get/id/" + currentReservationId;
        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint).header("Authorization", getAuthorizationToken(DIFFERENT_CUSTOMER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), endpoint,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

}
