package com.s1gawron.rentalservice.tool.controller.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetToolControllerIntegrationTest extends AbstractToolControllerIntegrationTest {

    private static final String TOOL_GET_ENDPOINT = "/api/public/tool/v1/get/";

    @Test
    void shouldGetNotRemovedToolsByCategoryWhenUserIsUnauthenticated() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createHeavyTools());
        saveToolsForTest(ToolCreatorHelper.I.createLightTools());
        saveToolsForTest(ToolCreatorHelper.I.createRemovedTools());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "category/HEAVY");

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolListingDTO resultObject = objectMapper.readValue(resultJson, ToolListingDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(3, resultObject.totalNumberOfTools());
        assertEquals(3, resultObject.tools().size());
    }

    @Test
    void shouldGetNotRemovedToolsByCategoryWhenUserIsCustomer() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createHeavyTools());
        saveToolsForTest(ToolCreatorHelper.I.createLightTools());
        saveToolsForTest(ToolCreatorHelper.I.createRemovedTools());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "category/HEAVY")
            .header("Authorization", getAuthorizationToken(UserRole.CUSTOMER));

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolListingDTO resultObject = objectMapper.readValue(resultJson, ToolListingDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(3, resultObject.totalNumberOfTools());
        assertEquals(3, resultObject.tools().size());
    }

    @Test
    void shouldGetAllToolsByCategoryWhenUserIsWorker() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createHeavyTools());
        saveToolsForTest(ToolCreatorHelper.I.createLightTools());
        saveToolsForTest(ToolCreatorHelper.I.createRemovedTools());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "category/HEAVY")
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolListingDTO resultObject = objectMapper.readValue(resultJson, ToolListingDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(6, resultObject.totalNumberOfTools());
        assertEquals(6, resultObject.tools().size());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolCategoryDoesNotExist() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createHeavyTools());
        saveToolsForTest(ToolCreatorHelper.I.createLightTools());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "category/unknown");

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    void shouldGetNewTools() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createHeavyToolsWithDate());
        saveToolsForTest(ToolCreatorHelper.I.createLightToolsWithDate());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "new");

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final List<ToolDetailsDTO> resultList = objectMapper.readValue(resultJson, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(3, resultList.size());

        final long areProperToolsInResultListCount = resultList.stream().filter(tool -> {
                final String toolName = tool.name();
                return toolName.equals("Loader") || toolName.equals("Crane") || toolName.equals("Big hammer");
            })
            .count();

        assertEquals(3, areProperToolsInResultListCount);
    }

    @Test
    void shouldGetToolById() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createHeavyTools());
        saveToolsForTest(ToolCreatorHelper.I.createLightTools());
        final Tool chainsaw = ToolCreatorHelper.I.createChainsaw();
        saveToolForTest(chainsaw);

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "id/" + chainsaw.getToolId());

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolDetailsDTO resultObject = objectMapper.readValue(resultJson, ToolDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(chainsaw.getToolId(), resultObject.toolId());
        assertEquals(chainsaw.getName(), resultObject.name());
    }

    @Test
    void shouldReturnNotFoundResponseWhenToolIsNotFoundById() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createHeavyTools());
        saveToolsForTest(ToolCreatorHelper.I.createLightTools());

        final Optional<Tool> toolById = toolDAO.findById(99L);

        if (toolById.isPresent()) {
            throw new IllegalStateException("Tool cannot be in database, because it was not added!");
        }

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "id/99");

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    void shouldGetNotRemovedToolsByNameWhenUserIsUnauthenticated() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createCommonNameToolList(false));
        saveToolsForTest(ToolCreatorHelper.I.createCommonNameToolList(true));
        saveToolForTest(ToolCreatorHelper.I.createChainsaw());

        final String json = """
            {
              "toolName": "hammer"
            }""";

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_GET_ENDPOINT + "name").contentType(MediaType.APPLICATION_JSON).content(json);

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolListingDTO resultList = objectMapper.readValue(resultJson, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(1, resultList.numberOfPages());
        assertEquals(2, resultList.totalNumberOfTools());

        for (final ToolDetailsDTO details : resultList.tools()) {
            assertTrue(details.name().toLowerCase().contains("hammer"));
        }
    }

    @Test
    void shouldGetNotRemovedToolsByNameWhenUserIsCustomer() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createCommonNameToolList(false));
        saveToolsForTest(ToolCreatorHelper.I.createCommonNameToolList(true));
        saveToolForTest(ToolCreatorHelper.I.createChainsaw());

        final String json = """
            {
              "toolName": "hammer"
            }
            """;

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_GET_ENDPOINT + "name").contentType(MediaType.APPLICATION_JSON).content(json)
            .header("Authorization", getAuthorizationToken(UserRole.CUSTOMER));

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolListingDTO resultList = objectMapper.readValue(resultJson, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(1, resultList.numberOfPages());
        assertEquals(2, resultList.totalNumberOfTools());

        for (final ToolDetailsDTO details : resultList.tools()) {
            assertTrue(details.name().toLowerCase().contains("hammer"));
        }
    }

    @Test
    void shouldGetAllToolsByNameWhenUserIsWorker() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createCommonNameToolList(false));
        saveToolsForTest(ToolCreatorHelper.I.createCommonNameToolList(true));
        saveToolForTest(ToolCreatorHelper.I.createChainsaw());

        final String json = """
            {
              "toolName": "hammer"
            }
            """;

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_GET_ENDPOINT + "name").contentType(MediaType.APPLICATION_JSON).content(json)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolListingDTO resultList = objectMapper.readValue(resultJson, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(1, resultList.numberOfPages());
        assertEquals(4, resultList.totalNumberOfTools());
        assertEquals(2, resultList.tools().stream().filter(tool -> !tool.removed()).count());
        assertEquals(2, resultList.tools().stream().filter(ToolDetailsDTO::removed).count());

        for (final ToolDetailsDTO details : resultList.tools()) {
            assertTrue(details.name().toLowerCase().contains("hammer"));
        }
    }

    @Test
    void shouldReturnEmptyListWhenToolsAreNotFoundByName() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createCommonNameToolList(false));

        final String json = """
            {
              "toolName": "chainsaw"
            }
            """;

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_GET_ENDPOINT + "name").contentType(MediaType.APPLICATION_JSON).content(json);
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolListingDTO resultList = objectMapper.readValue(resultJson, new TypeReference<>() {

        });

        assertEquals(0, resultList.numberOfPages());
        assertEquals(0, resultList.totalNumberOfTools());
    }

    @Test
    void shouldGetNotRemovedToolsIfUserIsUnauthenticated() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createToolList());
        saveToolsForTest(ToolCreatorHelper.I.createRemovedTools());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "all");
        final MockHttpServletResponse result = mockMvc.perform(request).andReturn().getResponse();
        final ToolListingDTO resultObject = objectMapper.readValue(result.getContentAsString(), ToolListingDTO.class);
        final long removedToolsCount = resultObject.tools().stream().filter(ToolDetailsDTO::removed).count();
        final long notRemovedToolsCount = resultObject.tools().stream().filter(tool -> !tool.removed()).count();

        assertEquals(HttpStatus.OK.value(), result.getStatus());
        assertEquals(3, resultObject.totalNumberOfTools());
        assertEquals(3, resultObject.tools().size());
        assertEquals(0, removedToolsCount);
        assertEquals(3, notRemovedToolsCount);
    }

    @Test
    void shouldGetNotRemovedToolsIfUserIsCustomer() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createToolList());
        saveToolsForTest(ToolCreatorHelper.I.createRemovedTools());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "all")
            .header("Authorization", getAuthorizationToken(UserRole.CUSTOMER));
        final MockHttpServletResponse result = mockMvc.perform(request).andReturn().getResponse();
        final ToolListingDTO resultObject = objectMapper.readValue(result.getContentAsString(), ToolListingDTO.class);
        final long removedToolsCount = resultObject.tools().stream().filter(ToolDetailsDTO::removed).count();
        final long notRemovedToolsCount = resultObject.tools().stream().filter(tool -> !tool.removed()).count();

        assertEquals(HttpStatus.OK.value(), result.getStatus());
        assertEquals(3, resultObject.totalNumberOfTools());
        assertEquals(3, resultObject.tools().size());
        assertEquals(0, removedToolsCount);
        assertEquals(3, notRemovedToolsCount);
    }

    @Test
    void shouldGetAllToolsIfUserIsWorker() throws Exception {
        saveToolsForTest(ToolCreatorHelper.I.createToolList());
        saveToolsForTest(ToolCreatorHelper.I.createRemovedTools());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "all")
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));
        final MockHttpServletResponse result = mockMvc.perform(request).andReturn().getResponse();
        final ToolListingDTO resultObject = objectMapper.readValue(result.getContentAsString(), ToolListingDTO.class);
        final long removedToolsCount = resultObject.tools().stream().filter(ToolDetailsDTO::removed).count();
        final long notRemovedToolsCount = resultObject.tools().stream().filter(tool -> !tool.removed()).count();

        assertEquals(HttpStatus.OK.value(), result.getStatus());
        assertEquals(9, resultObject.totalNumberOfTools());
        assertEquals(9, resultObject.tools().size());
        assertEquals(6, removedToolsCount);
        assertEquals(3, notRemovedToolsCount);
    }

}
