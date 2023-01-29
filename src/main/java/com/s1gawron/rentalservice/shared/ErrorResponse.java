package com.s1gawron.rentalservice.shared;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "timestamp", "code", "error", "message", "URI" })
public record ErrorResponse(String timestamp, int code, String error, String message, String URI) {

}
