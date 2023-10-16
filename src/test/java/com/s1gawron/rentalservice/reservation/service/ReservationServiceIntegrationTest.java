package com.s1gawron.rentalservice.reservation.service;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.dto.ReservationDetailsDTO;
import com.s1gawron.rentalservice.shared.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.shared.usercontext.UserContextProvider;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.service.ToolService;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class ReservationServiceIntegrationTest {

    @Autowired
    private ToolService toolService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    @Test
    void shouldNotChangePriceOnReservationWhenToolPriceChanges() {
        final ToolDTO toolDTO = ToolCreatorHelper.I.createToolDTO();
        final ToolDetailsDTO toolBeforePriceChange = toolService.validateAndAddTool(toolDTO);

        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO customerRegisterDTO = new UserRegisterDTO("test@test.pl", "Start00!", "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        final User user = User.createUser(customerRegisterDTO, "Start00!");
        userService.saveUser(user);

        UserContextProvider.I.setLoggedInUser(user);

        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(7), "", List.of(1L));
        reservationService.makeReservation(reservationDTO);

        final ReservationDetailsDTO reservationDetailsBeforeToolPriceChange = reservationService.getReservationDetails(1L);

        final BigDecimal toolPriceOnReservation = BigDecimal.valueOf(10.99);
        assertEquals(toolPriceOnReservation, reservationDetailsBeforeToolPriceChange.tools().get(0).price());
        assertEquals(toolPriceOnReservation, reservationDetailsBeforeToolPriceChange.reservationFinalPrice());

        final BigDecimal newToolPrice = BigDecimal.valueOf(20.00);
        final ToolDetailsDTO toolAfterPriceChange = new ToolDetailsDTO(toolBeforePriceChange.toolId(), toolBeforePriceChange.available(),
            toolBeforePriceChange.removed(), toolBeforePriceChange.name(), toolBeforePriceChange.description(), toolBeforePriceChange.toolCategory(),
            newToolPrice, toolBeforePriceChange.toolState(), toolBeforePriceChange.imageUrl());
        toolService.validateAndEditTool(toolAfterPriceChange);

        final ReservationDetailsDTO reservationDetailsAfterToolPriceChange = reservationService.getReservationDetails(1L);
        assertEquals(toolPriceOnReservation, reservationDetailsAfterToolPriceChange.tools().get(0).price());
        assertEquals(toolPriceOnReservation, reservationDetailsAfterToolPriceChange.reservationFinalPrice());

        UserContextProvider.I.clearLoggedInUser();
    }

}
