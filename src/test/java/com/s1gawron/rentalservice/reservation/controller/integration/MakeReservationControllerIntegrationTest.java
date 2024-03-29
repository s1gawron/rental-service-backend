package com.s1gawron.rentalservice.reservation.controller.integration;

import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.exception.DateMismatchException;
import com.s1gawron.rentalservice.reservation.exception.ReservationEmptyPropertiesException;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.exception.ToolRemovedException;
import com.s1gawron.rentalservice.tool.exception.ToolUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MakeReservationControllerIntegrationTest extends AbstractReservationControllerIntegrationTest {

    @Test
    void shouldMakeReservation() throws Exception {
        final String loaderReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now() + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now().plusDays(3L) + "\",\n"
            + "  \"additionalComment\": \"Loader\",\n"
            + "  \"toolIds\": [\n"
            + "    " + loaderToolId + "\n"
            + "  ]\n"
            + "}";

        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(loaderReservationJson)
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ReservationDetailsDTO resultObject = objectMapper.readValue(resultJson, ReservationDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(1, resultObject.tools().size());
        assertEquals(loaderToolId, resultObject.tools().get(0).toolId());
        assertEquals("Loader", resultObject.tools().get(0).name());
        assertEquals(BigDecimal.valueOf(1000.99), resultObject.reservationFinalPrice());
        assertFalse(toolService.getToolById(loaderToolId).isAvailable());
    }

    @Test
    void shouldMakeReservationWithMultipleTools() throws Exception {
        final String loaderReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now() + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now().plusDays(3L) + "\",\n"
            + "  \"additionalComment\": \"Hammer, chainsaw and loader\",\n"
            + "  \"toolIds\": [\n"
            + "    " + loaderToolId + ",\n"
            + "    " + currentToolId + ",\n"
            + "    " + nextToolId + "\n"
            + "  ]\n"
            + "}";

        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(loaderReservationJson)
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ReservationDetailsDTO resultObject = objectMapper.readValue(resultJson, ReservationDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(3, resultObject.tools().size());
        assertEquals(BigDecimal.valueOf(1112.97), resultObject.reservationFinalPrice());
        assertFalse(toolService.getToolById(loaderToolId).isAvailable());
        assertFalse(toolService.getToolById(currentToolId).isAvailable());
        assertFalse(toolService.getToolById(nextToolId).isAvailable());
    }

    @Test
    void shouldReturnForbiddentResponseWhenUserIsWorkerWhileMakeReservation() throws Exception {
        final String loaderReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now() + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now().plusDays(3L) + "\",\n"
            + "  \"additionalComment\": \"Loader\",\n"
            + "  \"toolIds\": [\n"
            + "    " + loaderToolId + "\n"
            + "  ]\n"
            + "}";

        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(loaderReservationJson)
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", getAuthorizationToken(WORKER_EMAIL));

        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenResponseWhenUserIsUnauthenticated() throws Exception {
        final String loaderReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now() + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now().plusDays(3L) + "\",\n"
            + "  \"additionalComment\": \"Loader\",\n"
            + "  \"toolIds\": [\n"
            + "    " + loaderToolId + "\n"
            + "  ]\n"
            + "}";

        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(loaderReservationJson)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnBadRequestResponseWhenReservationHasEmptyProperties() throws Exception {
        final String loaderReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now() + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now().plusDays(3L) + "\",\n"
            + "  \"additionalComment\": \"Hammer, chainsaw and loader\"\n"
            + "}";

        final ReservationEmptyPropertiesException expectedException = ReservationEmptyPropertiesException.createForToolsList();
        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(loaderReservationJson)
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenDateFromIsBeforeCurrentDate() throws Exception {
        final String loaderReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now().minusDays(3L) + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now().plusDays(1L) + "\",\n"
            + "  \"additionalComment\": \"Hammer, chainsaw and loader\",\n"
            + "  \"toolIds\": [\n"
            + "    " + loaderToolId + ",\n"
            + "    " + currentToolId + ",\n"
            + "    " + nextToolId + "\n"
            + "  ]\n"
            + "}";

        final DateMismatchException expectedException = DateMismatchException.createForDateFrom();
        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(loaderReservationJson)
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenDateFromIsAfterDueDate() throws Exception {
        final String loaderReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now().plusDays(3L) + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now() + "\",\n"
            + "  \"additionalComment\": \"Hammer, chainsaw and loader\",\n"
            + "  \"toolIds\": [\n"
            + "    " + loaderToolId + ",\n"
            + "    " + currentToolId + ",\n"
            + "    " + nextToolId + "\n"
            + "  ]\n"
            + "}";

        final DateMismatchException expectedException = DateMismatchException.createForDateFromIsAfterDueDate();
        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(loaderReservationJson)
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolIsNotAvailable() throws Exception {
        performMakeReservationRequests();
        final String loaderReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now() + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now().plusDays(3L) + "\",\n"
            + "  \"additionalComment\": \"Hammer, chainsaw and loader\",\n"
            + "  \"toolIds\": [\n"
            + "    " + loaderToolId + ",\n"
            + "    " + currentToolId + ",\n"
            + "    " + nextToolId + "\n"
            + "  ]\n"
            + "}";

        final ToolUnavailableException expectedException = ToolUnavailableException.create(currentToolId);
        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(loaderReservationJson)
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolIsRemoved() throws Exception {
        final String loaderReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now() + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now().plusDays(3L) + "\",\n"
            + "  \"additionalComment\": \"Removed ha,,er\",\n"
            + "  \"toolIds\": [\n"
            + "    " + removedToolId + "\n"
            + "  ]\n"
            + "}";

        final ToolRemovedException expectedException = ToolRemovedException.create(removedToolId);
        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(loaderReservationJson)
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnNotFoundResponseWhenToolDoesNotExist() throws Exception {
        final long notExistingToolId = 99999L;
        final String loaderReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now() + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now().plusDays(3L) + "\",\n"
            + "  \"additionalComment\": \"Removed ha,,er\",\n"
            + "  \"toolIds\": [\n"
            + "    " + notExistingToolId + "\n"
            + "  ]\n"
            + "}";

        final ToolNotFoundException expectedException = ToolNotFoundException.create(notExistingToolId);
        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(loaderReservationJson)
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));

        mockMvc.perform(request)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

}
