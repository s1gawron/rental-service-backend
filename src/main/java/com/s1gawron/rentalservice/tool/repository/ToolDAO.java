package com.s1gawron.rentalservice.tool.repository;

import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ToolDAO {

    Page<Tool> findAllByToolCategory(final ToolCategory toolCategory, final Pageable pageable);

    Page<Tool> findAllByToolCategory(final ToolCategory toolCategory, final boolean removed, final Pageable pageable);

    List<Tool> findNewTools();

    Page<Tool> findByName(final String toolName, final Pageable pageable);

    Page<Tool> findNotRemovedToolsByName(final String toolName, final Pageable pageable);

    Optional<Boolean> isToolAvailable(final long toolId);

    Optional<Boolean> isToolRemoved(final long toolId);

    List<Tool> findAll();

    Page<Tool> findAll(final boolean removed, final Pageable pageable);

    Page<Tool> findAllWithLimit(final Pageable pageable);

    Optional<Tool> findById(final Long toolId);

    Tool save(final Tool tool);

}
