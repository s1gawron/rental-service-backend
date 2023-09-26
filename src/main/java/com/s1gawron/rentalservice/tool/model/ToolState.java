package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;

import jakarta.persistence.*;

@Embeddable
public class ToolState {

    @Enumerated(EnumType.STRING)
    private ToolStateType stateType;

    private String stateDescription;

    public ToolState() {
    }

    public ToolState(final ToolStateType stateType, final String stateDescription) {
        this.stateType = stateType;
        this.stateDescription = stateDescription;
    }

    public static ToolState from(final ToolStateDTO toolState) {
        return new ToolState(ToolStateType.findByValue(toolState.stateType()), toolState.description());
    }

    public ToolStateType getStateType() {
        return stateType;
    }

    public String getStateDescription() {
        return stateDescription;
    }
}
