package com.s1gawron.rentalservice.tool.model;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "tool_state")
@DynamicUpdate
@NoArgsConstructor
public class ToolState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tool_state_id")
    private Long toolStateId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "state_type")
    private ToolStateType stateType;

}
