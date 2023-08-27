package com.s1gawron.rentalservice.tool.dto;

import java.util.List;

public record ToolListingDTO(int numberOfPages, int totalNumberOfTools, List<ToolDetailsDTO> tools) {

}
