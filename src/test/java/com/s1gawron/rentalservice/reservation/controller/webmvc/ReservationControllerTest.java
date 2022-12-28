package com.s1gawron.rentalservice.reservation.controller.webmvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.jwt.JwtConfig;
import com.s1gawron.rentalservice.reservation.controller.ReservationController;
import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.exception.DateMismatchException;
import com.s1gawron.rentalservice.reservation.exception.ReservationEmptyPropertiesException;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import com.s1gawron.rentalservice.reservation.helper.ReservationCreatorHelper;
import com.s1gawron.rentalservice.reservation.service.ReservationService;
import com.s1gawron.rentalservice.shared.ErrorResponse;
import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.exception.ToolUnavailableException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(ReservationController.class)
@ActiveProfiles("test")
@WithMockUser
class ReservationControllerTest {

    private static final String RESERVATION_ENDPOINT = "/api/customer/reservation/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSource dataSourceMock;

    @MockBean
    private JwtConfig jwtConfigMock;

    @MockBean
    private ReservationService reservationServiceMock;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void shouldGetUserReservations() {
        final List<ReservationDetailsDTO> reservationDetailsList = ReservationCreatorHelper.I.createReservationDetailsList();

        Mockito.when(reservationServiceMock.getUserReservations()).thenReturn(reservationDetailsList);

        final RequestBuilder request = MockMvcRequestBuilders.get(RESERVATION_ENDPOINT + "get/all");
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final List<ReservationDetailsDTO> reservationDetailsListResult = objectMapper.readValue(jsonResult, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(reservationDetailsListResult);
        assertEquals(3, reservationDetailsListResult.size());
    }

    @Test
    @SneakyThrows
    void shouldReturnForbiddenResponseWhenUserIsNotAllowedToGetUserReservations() {
        final NoAccessForUserRoleException expectedException = NoAccessForUserRoleException.create("CUSTOMER RESERVATIONS");

        Mockito.when(reservationServiceMock.getUserReservations()).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "get/all";
        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.FORBIDDEN, expectedException.getMessage(), endpoint, toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldGetReservationById() {
        final ReservationDetailsDTO reservationDetailsDTO = ReservationCreatorHelper.I.createReservationDetailsDTO();

        Mockito.when(reservationServiceMock.getReservationDetails(1L)).thenReturn(reservationDetailsDTO);

        final RequestBuilder request = MockMvcRequestBuilders.get(RESERVATION_ENDPOINT + "get/id/1");
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final ReservationDetailsDTO reservationDetailsDTOResult = objectMapper.readValue(jsonResult, ReservationDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(reservationDetailsDTOResult);
        assertEquals("Hammer", reservationDetailsDTOResult.getAdditionalComment());
        assertEquals("Hammer", reservationDetailsDTOResult.getTools().get(0).getName());
    }

    @Test
    @SneakyThrows
    void shouldReturnForbiddenResponseWhenUserIsNotAllowedToGetReservationDetails() {
        final NoAccessForUserRoleException expectedException = NoAccessForUserRoleException.create("CUSTOMER RESERVATIONS");

        Mockito.when(reservationServiceMock.getReservationDetails(1L)).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "get/id/1";
        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.FORBIDDEN, expectedException.getMessage(), endpoint, toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnNotFoundResponseWhenReservationWasNotFoundWhileGettingReservationDetails() {
        final ReservationNotFoundException expectedException = ReservationNotFoundException.create(1L);

        Mockito.when(reservationServiceMock.getReservationDetails(1L)).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "get/id/1";
        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), endpoint, toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldMakeReservation() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(3L), "Hammer", List.of(1L));
        final ReservationDetailsDTO reservationDetailsDTO = ReservationCreatorHelper.I.createReservationDetailsDTO();
        final String reservationDTOJson = objectMapper.writeValueAsString(reservationDTO);

        Mockito.when(reservationServiceMock.makeReservation(Mockito.any(ReservationDTO.class))).thenReturn(reservationDetailsDTO);

        final RequestBuilder request = MockMvcRequestBuilders.post(RESERVATION_ENDPOINT + "make").content(reservationDTOJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final ReservationDetailsDTO reservationDetailsDTOResult = objectMapper.readValue(jsonResult, ReservationDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(reservationDetailsDTOResult);
        assertEquals(1, reservationDetailsDTOResult.getReservationId());
        assertEquals(BigDecimal.valueOf(10.99), reservationDetailsDTOResult.getReservationFinalPrice());
        assertEquals("Hammer", reservationDetailsDTOResult.getAdditionalComment());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenReservationHasEmptyProperties() {
        final ReservationEmptyPropertiesException expectedException = ReservationEmptyPropertiesException.createForDateFrom();
        final ReservationDTO reservationDTO = new ReservationDTO(null, LocalDate.now().plusDays(3L), "Hammer", List.of(1L));
        final String reservationDTOJson = objectMapper.writeValueAsString(reservationDTO);

        Mockito.when(reservationServiceMock.makeReservation(Mockito.any(ReservationDTO.class))).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "make";
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).content(reservationDTOJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), endpoint, toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenReservationDueDateIsBeforeCurrentDate() {
        final DateMismatchException expectedException = DateMismatchException.createForDateTo();
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().minusDays(3L), "Hammer", List.of(1L));
        final String reservationDTOJson = objectMapper.writeValueAsString(reservationDTO);

        Mockito.when(reservationServiceMock.makeReservation(Mockito.any(ReservationDTO.class))).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "make";
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).content(reservationDTOJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), endpoint, toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnForbiddenResponseWhenUserIsNotAllowedToMakeReservation() {
        final NoAccessForUserRoleException expectedException = NoAccessForUserRoleException.create("CUSTOMER RESERVATIONS");
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(3L), "Hammer", List.of(1L));
        final String reservationDTOJson = objectMapper.writeValueAsString(reservationDTO);

        Mockito.when(reservationServiceMock.makeReservation(Mockito.any(ReservationDTO.class))).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "make";
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).content(reservationDTOJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.FORBIDDEN, expectedException.getMessage(), endpoint, toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnNotFoundResponseWhenToolIsNotFoundWhileMakingReservation() {
        final ToolNotFoundException expectedException = ToolNotFoundException.create(1L);
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(3L), "Hammer", List.of(1L));
        final String reservationDTOJson = objectMapper.writeValueAsString(reservationDTO);

        Mockito.when(reservationServiceMock.makeReservation(Mockito.any(ReservationDTO.class))).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "make";
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).content(reservationDTOJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), endpoint, toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolIsNotAvailableWhileMakingReservation() {
        final ToolUnavailableException expectedException = ToolUnavailableException.create(1L);
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(3L), "Hammer", List.of(1L));
        final String reservationDTOJson = objectMapper.writeValueAsString(reservationDTO);

        Mockito.when(reservationServiceMock.makeReservation(Mockito.any(ReservationDTO.class))).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "make";
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).content(reservationDTOJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), endpoint, toErrorResponse(result.getResponse().getContentAsString()));
    }

    void assertErrorResponse(final HttpStatus expectedStatus, final String expectedMessage, final String expectedUri,
        final ErrorResponse actualErrorResponse) {
        assertEquals(expectedStatus.value(), actualErrorResponse.getCode());
        assertEquals(expectedStatus.getReasonPhrase(), actualErrorResponse.getError());
        assertEquals(expectedMessage, actualErrorResponse.getMessage());
        assertEquals(expectedUri, actualErrorResponse.getURI());
    }

    @SneakyThrows
    ErrorResponse toErrorResponse(final String responseMessage) {
        return objectMapper.readValue(responseMessage, ErrorResponse.class);
    }

}