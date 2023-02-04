package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;

@Entity
@Table(name = "tool_state")
@DynamicUpdate
public class ToolState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tool_state_id")
    private Long toolStateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state_type")
    private ToolStateType stateType;

    @Column(name = "description")
    private String description;

    public ToolState() {
    }

    public ToolState(final ToolStateType stateType, final String description) {
        this.stateType = stateType;
        this.description = description;
    }

    public static ToolState from(final ToolStateDTO toolState) {
        return new ToolState(ToolStateType.findByValue(toolState.stateType()), toolState.description());
    }

    public void edit(final ToolStateDTO toolState) {
        this.stateType = ToolStateType.findByValue(toolState.stateType());
        this.description = toolState.description();
    }

    public ToolStateType getStateType() {
        return stateType;
    }

    public String getDescription() {
        return description;
    }
}
