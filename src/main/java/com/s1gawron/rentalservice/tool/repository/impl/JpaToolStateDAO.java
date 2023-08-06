package com.s1gawron.rentalservice.tool.repository.impl;

import com.s1gawron.rentalservice.tool.model.ToolState;
import com.s1gawron.rentalservice.tool.repository.ToolStateDAO;
import org.springframework.stereotype.Repository;

@Repository
public class JpaToolStateDAO implements ToolStateDAO {

    private final ToolStateJpaRepository toolStateJpaRepository;

    public JpaToolStateDAO(final ToolStateJpaRepository toolStateJpaRepository) {
        this.toolStateJpaRepository = toolStateJpaRepository;
    }

    @Override public ToolState save(final ToolState toolState) {
        return toolStateJpaRepository.save(toolState);
    }

}
