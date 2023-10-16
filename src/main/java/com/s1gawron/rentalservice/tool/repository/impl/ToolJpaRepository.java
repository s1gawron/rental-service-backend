package com.s1gawron.rentalservice.tool.repository.impl;

import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ToolJpaRepository extends JpaRepository<Tool, Long> {

    Page<Tool> findAllByToolCategory(final ToolCategory toolCategory, final Pageable pageable);

    Page<Tool> findAllByToolCategoryAndRemoved(final ToolCategory toolCategory, final boolean removed, final Pageable pageable);

    @Query(value = "SELECT t FROM Tool t WHERE t.removed = false ORDER BY t.dateAdded DESC LIMIT 3")
    List<Tool> findNewTools();

    @Query(value = "SELECT t FROM Tool t WHERE UPPER(t.name) LIKE UPPER(concat('%', :toolName, '%'))")
    Page<Tool> findByName(final String toolName, final Pageable pageable);

    @Query(value = "SELECT t FROM Tool t WHERE UPPER(t.name) LIKE UPPER(concat('%', :toolName, '%')) AND t.removed = FALSE")
    Page<Tool> findNotRemovedToolsByName(@Param(value = "toolName") final String toolName, final Pageable pageable);

    @Query(value = "SELECT t.available FROM Tool t WHERE t.toolId = :toolId")
    Optional<Boolean> isToolAvailable(@Param(value = "toolId") final long toolId);

    @Query(value = "SELECT t.removed FROM Tool t WHERE t.toolId = :toolId")
    Optional<Boolean> isToolRemoved(@Param(value = "toolId") final long toolId);

    @Query(value = "SELECT t FROM Tool t WHERE t.removed = :removed")
    Page<Tool> findAll(@Param(value = "removed") final boolean removed, final Pageable pageable);

    @Query(value = "SELECT t FROM Tool t")
    Page<Tool> findAllWithLimit(final Pageable pageable);

}
