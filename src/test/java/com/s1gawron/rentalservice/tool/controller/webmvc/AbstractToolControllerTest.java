package com.s1gawron.rentalservice.tool.controller.webmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.security.JwtService;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.service.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

abstract class AbstractToolControllerTest {

    protected static final String ERROR_RESPONSE_MESSAGE_PLACEHOLDER = "$.message";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ToolService toolServiceMock;

    @MockBean
    JwtService jwtServiceMock;

    final ObjectMapper objectMapper = ObjectMapperCreator.I.getMapper();

    int getToolListSizeFilteredByCategory(final ToolCategory expected, final List<ToolDetailsDTO> tools) {
        return (int) tools.stream().filter(tool -> tool.toolCategory().equals(expected.name())).count();
    }

}
