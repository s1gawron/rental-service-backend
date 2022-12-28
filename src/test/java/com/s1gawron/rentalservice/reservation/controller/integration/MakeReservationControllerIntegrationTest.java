package com.s1gawron.rentalservice.reservation.controller.integration;

import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.exception.DateMismatchException;
import com.s1gawron.rentalservice.reservation.exception.ReservationEmptyPropertiesException;
import com.s1gawron.rentalservice.tool.exception.ToolUnavailableException;
import lombok.SneakyThrows;
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

class MakeReservationControllerIntegrationTest extends AbstractReservationControllerIntegrationTest {

    @Test
    @SneakyThrows
    void shouldMakeReservation() {
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
        assertEquals(1, resultObject.getTools().size());
        assertEquals(loaderToolId, resultObject.getTools().get(0).getToolId());
        assertEquals("Loader", resultObject.getTools().get(0).getName());
        assertEquals(BigDecimal.valueOf(1000.99), resultObject.getReservationFinalPrice());
        assertFalse(toolService.getToolById(loaderToolId).isAvailable());
    }

    @Test
    @SneakyThrows
    void shouldMakeReservationWithMultipleTools() {
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
        assertEquals(3, resultObject.getTools().size());
        assertEquals(BigDecimal.valueOf(1112.97), resultObject.getReservationFinalPrice());
        assertFalse(toolService.getToolById(loaderToolId).isAvailable());
        assertFalse(toolService.getToolById(currentToolId).isAvailable());
        assertFalse(toolService.getToolById(nextToolId).isAvailable());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenReservationHasEmptyProperties() {
        final String loaderReservationJson = "{\n"
            + "  \"dateFrom\": \"" + LocalDate.now() + "\",\n"
            + "  \"dateTo\": \"" + LocalDate.now().plusDays(3L) + "\",\n"
            + "  \"additionalComment\": \"Hammer, chainsaw and loader\"\n"
            + "}";

        final ReservationEmptyPropertiesException expectedException = ReservationEmptyPropertiesException.createForToolsList();
        final RequestBuilder request = MockMvcRequestBuilders.post(MAKE_RESERVATION_ENDPOINT).content(loaderReservationJson)
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", getAuthorizationToken(CUSTOMER_EMAIL));
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), MAKE_RESERVATION_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenDateFromIsBeforeCurrentDate() {
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
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), MAKE_RESERVATION_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenDateFromIsAfterDueDate() {
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
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), MAKE_RESERVATION_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolIsNotAvailable() {
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
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), MAKE_RESERVATION_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

}
