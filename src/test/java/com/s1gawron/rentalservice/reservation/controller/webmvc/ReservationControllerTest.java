package com.s1gawron.rentalservice.reservation.controller.webmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.reservation.controller.ReservationController;
import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationListingDTO;
import com.s1gawron.rentalservice.reservation.exception.DateMismatchException;
import com.s1gawron.rentalservice.reservation.exception.ReservationEmptyPropertiesException;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import com.s1gawron.rentalservice.reservation.helper.ReservationCreatorHelper;
import com.s1gawron.rentalservice.reservation.service.ReservationService;
import com.s1gawron.rentalservice.security.JwtService;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.exception.ToolRemovedException;
import com.s1gawron.rentalservice.tool.exception.ToolUnavailableException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@ActiveProfiles("test")
@WithMockUser
class ReservationControllerTest {

    private static final String ERROR_RESPONSE_MESSAGE_PLACEHOLDER = "$.message";

    private static final String RESERVATION_ENDPOINT = "/api/customer/reservation/v1/";

    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 25);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationServiceMock;

    @MockBean
    private JwtService jwtServiceMock;

    private final ObjectMapper objectMapper = ObjectMapperCreator.I.getMapper();

    @Test
    void shouldGetUserReservations() throws Exception {
        final List<ReservationDetailsDTO> reservationDetailsList = ReservationCreatorHelper.I.createReservationDetailsList();
        final ReservationListingDTO reservationListingDTO = new ReservationListingDTO(1, reservationDetailsList.size(), reservationDetailsList);

        Mockito.when(reservationServiceMock.getUserReservations(DEFAULT_PAGEABLE)).thenReturn(reservationListingDTO);

        final RequestBuilder request = MockMvcRequestBuilders.get(RESERVATION_ENDPOINT + "get/all");
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final ReservationListingDTO reservationDetailsListResult = objectMapper.readValue(jsonResult, ReservationListingDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(reservationDetailsListResult);
        assertEquals(1, reservationDetailsListResult.numberOfPages());
        assertEquals(3, reservationDetailsListResult.totalNumberOfReservations());
    }

    @Test
    void shouldGetReservationById() throws Exception {
        final ReservationDetailsDTO reservationDetailsDTO = ReservationCreatorHelper.I.createReservationDetailsDTO();

        Mockito.when(reservationServiceMock.getReservationDetails(1L)).thenReturn(reservationDetailsDTO);

        final RequestBuilder request = MockMvcRequestBuilders.get(RESERVATION_ENDPOINT + "get/id/1");
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final ReservationDetailsDTO reservationDetailsDTOResult = objectMapper.readValue(jsonResult, ReservationDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(reservationDetailsDTOResult);
        assertEquals("Hammer", reservationDetailsDTOResult.additionalComment());
        assertEquals("Hammer", reservationDetailsDTOResult.tools().get(0).name());
    }

    @Test
    void shouldReturnNotFoundResponseWhenReservationWasNotFoundWhileGettingReservationDetails() throws Exception {
        final ReservationNotFoundException expectedException = ReservationNotFoundException.create(1L);

        Mockito.when(reservationServiceMock.getReservationDetails(1L)).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "get/id/1";
        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint);

        mockMvc.perform(request)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldMakeReservation() throws Exception {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(3L), "Hammer", List.of(1L));
        final ReservationDetailsDTO reservationDetailsDTO = ReservationCreatorHelper.I.createReservationDetailsDTO();
        final String reservationDTOJson = objectMapper.writeValueAsString(reservationDTO);

        Mockito.when(reservationServiceMock.makeReservation(Mockito.any(ReservationDTO.class))).thenReturn(reservationDetailsDTO);

        final RequestBuilder request = MockMvcRequestBuilders.post(RESERVATION_ENDPOINT + "make").with(csrf()).content(reservationDTOJson)
            .contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final ReservationDetailsDTO reservationDetailsDTOResult = objectMapper.readValue(jsonResult, ReservationDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(reservationDetailsDTOResult);
        assertEquals(1, reservationDetailsDTOResult.reservationId());
        assertEquals(BigDecimal.valueOf(10.99), reservationDetailsDTOResult.reservationFinalPrice());
        assertEquals("Hammer", reservationDetailsDTOResult.additionalComment());
    }

    @Test
    void shouldReturnBadRequestResponseWhenReservationHasEmptyProperties() throws Exception {
        final ReservationEmptyPropertiesException expectedException = ReservationEmptyPropertiesException.createForDateFrom();
        final ReservationDTO reservationDTO = new ReservationDTO(null, LocalDate.now().plusDays(3L), "Hammer", List.of(1L));
        final String reservationDTOJson = objectMapper.writeValueAsString(reservationDTO);

        Mockito.when(reservationServiceMock.makeReservation(Mockito.any(ReservationDTO.class))).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "make";
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).with(csrf()).content(reservationDTOJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenReservationDueDateIsBeforeCurrentDate() throws Exception {
        final DateMismatchException expectedException = DateMismatchException.createForDateTo();
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().minusDays(3L), "Hammer", List.of(1L));
        final String reservationDTOJson = objectMapper.writeValueAsString(reservationDTO);

        Mockito.when(reservationServiceMock.makeReservation(Mockito.any(ReservationDTO.class))).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "make";
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).with(csrf()).content(reservationDTOJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnNotFoundResponseWhenToolIsNotFoundWhileMakingReservation() throws Exception {
        final ToolNotFoundException expectedException = ToolNotFoundException.create(1L);
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(3L), "Hammer", List.of(1L));
        final String reservationDTOJson = objectMapper.writeValueAsString(reservationDTO);

        Mockito.when(reservationServiceMock.makeReservation(Mockito.any(ReservationDTO.class))).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "make";
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).with(csrf()).content(reservationDTOJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolIsNotAvailableWhileMakingReservation() throws Exception {
        final ToolUnavailableException expectedException = ToolUnavailableException.create(1L);
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(3L), "Hammer", List.of(1L));
        final String reservationDTOJson = objectMapper.writeValueAsString(reservationDTO);

        Mockito.when(reservationServiceMock.makeReservation(Mockito.any(ReservationDTO.class))).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "make";
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).with(csrf()).content(reservationDTOJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolIsRemovedWhileMakingReservation() throws Exception {
        final ToolRemovedException expectedException = ToolRemovedException.create(1L);
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(3L), "Hammer", List.of(1L));
        final String reservationDTOJson = objectMapper.writeValueAsString(reservationDTO);

        Mockito.when(reservationServiceMock.makeReservation(Mockito.any(ReservationDTO.class))).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "make";
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).with(csrf()).content(reservationDTOJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldCancelReservation() throws Exception {
        final ReservationDetailsDTO canceledReservation = ReservationCreatorHelper.I.createCanceledReservationDetailsDTO();

        Mockito.when(reservationServiceMock.cancelReservation(1L)).thenReturn(canceledReservation);

        final RequestBuilder request = MockMvcRequestBuilders.post(RESERVATION_ENDPOINT + "cancel/1").with(csrf());

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final ReservationDetailsDTO reservationDetailsDTOResult = objectMapper.readValue(jsonResult, ReservationDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(reservationDetailsDTOResult);
        assertEquals("Hammer", reservationDetailsDTOResult.additionalComment());
        assertTrue(reservationDetailsDTOResult.reservationStatus().isCanceled());
        assertEquals("Hammer", reservationDetailsDTOResult.tools().get(0).name());
        assertTrue(reservationDetailsDTOResult.tools().get(0).available());
    }

    @Test
    void shouldReturnNotFoundResponseWhenReservationWasNotFoundWhileCancelingReservation() throws Exception {
        final ReservationNotFoundException expectedException = ReservationNotFoundException.create(1L);

        Mockito.when(reservationServiceMock.cancelReservation(1L)).thenThrow(expectedException);

        final String endpoint = RESERVATION_ENDPOINT + "cancel/1";
        final RequestBuilder request = MockMvcRequestBuilders.post(endpoint).with(csrf());

        mockMvc.perform(request)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

}