package com.s1gawron.rentalservice.tool.controller.webmvc;

import com.s1gawron.rentalservice.tool.controller.ToolController;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(ToolController.class)
@ActiveProfiles("test")
@WithMockUser
class ToolControllerTest extends AbstractToolControllerTest {

}
