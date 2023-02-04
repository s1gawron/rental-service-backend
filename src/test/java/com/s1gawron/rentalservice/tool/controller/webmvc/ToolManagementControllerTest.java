package com.s1gawron.rentalservice.tool.controller.webmvc;

import com.s1gawron.rentalservice.tool.controller.ToolManagementController;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(ToolManagementController.class)
@ActiveProfiles("test")
@WithMockUser
@AutoConfigureMockMvc(addFilters = false)
class ToolManagementControllerTest extends AbstractToolControllerTest {

}
