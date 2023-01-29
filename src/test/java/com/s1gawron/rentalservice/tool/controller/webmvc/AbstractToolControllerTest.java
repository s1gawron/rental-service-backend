package com.s1gawron.rentalservice.tool.controller.webmvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.jwt.JwtConfig;
import com.s1gawron.rentalservice.shared.ErrorResponse;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.service.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class AbstractToolControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DataSource dataSourceMock;

    @MockBean
    JwtConfig jwtConfigMock;

    @MockBean
    ToolService toolServiceMock;

    final ObjectMapper objectMapper = ObjectMapperCreator.I.getMapper();

    int getToolListSizeFilteredByCategory(final ToolCategory expected, final List<ToolDetailsDTO> tools) {
        return (int) tools.stream().filter(tool -> tool.toolCategory().equals(expected.name())).count();
    }

    void assertErrorResponse(final HttpStatus expectedStatus, final String expectedMessage, final String expectedUri,
        final ErrorResponse actualErrorResponse) {
        assertEquals(expectedStatus.value(), actualErrorResponse.code());
        assertEquals(expectedStatus.getReasonPhrase(), actualErrorResponse.error());
        assertEquals(expectedMessage, actualErrorResponse.message());
        assertEquals(expectedUri, actualErrorResponse.URI());
    }

    ErrorResponse toErrorResponse(final String responseMessage) throws JsonProcessingException {
        return objectMapper.readValue(responseMessage, ErrorResponse.class);
    }

}
