package com.s1gawron.rentalservice.user.controller.webmvc;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.user.controller.UserManagementController;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(UserManagementController.class)
@ActiveProfiles("test")
@WithMockUser
class GetUserControllerTest extends AbstractUserControllerTest {

    private static final String USER_DETAILS_ENDPOINT = "/api/user/";

    @Test
    void shouldGetUserDetails() throws Exception {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserDTO userDTO = new UserDTO("John", "Kowalski", "test@test.pl", UserRole.CUSTOMER.name(), addressDTO);

        Mockito.when(userServiceMock.getUserDetails()).thenReturn(userDTO);

        final RequestBuilder request = MockMvcRequestBuilders.get(USER_DETAILS_ENDPOINT + "details").content(userRegisterJson);
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final UserDTO userDTOResult = objectMapper.readValue(jsonResult, UserDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(userDTOResult);
        assertEquals(userDTO.email(), userDTOResult.email());
        assertEquals(userDTO.email(), userDTOResult.email());
        assertEquals(userDTO.customerAddress().country(), userDTOResult.customerAddress().country());
        assertEquals(userDTO.customerAddress().postCode(), userDTOResult.customerAddress().postCode());
    }

    @Test
    void shouldReturnNotFoundResponseWhenUserIsNotFoundWhileGettingUserDetails() throws Exception {
        final UserNotFoundException expectedException = UserNotFoundException.create("test@test.pl");
        final String endpoint = USER_DETAILS_ENDPOINT + "details";

        Mockito.when(userServiceMock.getUserDetails()).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint).content(userRegisterJson);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), endpoint, toErrorResponse(result.getResponse().getContentAsString()));
    }

}