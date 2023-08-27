package com.s1gawron.rentalservice.tool.repository.impl;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.repository.ToolDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaToolDAO implements ToolDAO {

    private final ToolJpaRepository toolJpaRepository;

    public JpaToolDAO(final ToolJpaRepository toolJpaRepository) {
        this.toolJpaRepository = toolJpaRepository;
    }

    @Override public Page<Tool> findAllByToolCategory(final ToolCategory toolCategory, final Pageable pageable) {
        return toolJpaRepository.findAllByToolCategory(toolCategory, pageable);
    }

    @Override public Page<Tool> findAllByToolCategory(final ToolCategory toolCategory, final boolean removed, final Pageable pageable) {
        return toolJpaRepository.findAllByToolCategoryAndRemoved(toolCategory, removed, pageable);
    }

    @Override public List<Tool> findNewTools() {
        return toolJpaRepository.findNewTools();
    }

    @Override public Page<Tool> findByName(final String toolName, final Pageable pageable) {
        return toolJpaRepository.findByName(toolName, pageable);
    }

    @Override public Page<Tool> findNotRemovedToolsByName(final String toolName, final Pageable pageable) {
        return toolJpaRepository.findNotRemovedToolsByName(toolName, pageable);
    }

    @Override public Optional<Boolean> isToolAvailable(final long toolId) {
        return toolJpaRepository.isToolAvailable(toolId);
    }

    @Override public Optional<Boolean> isToolRemoved(final long toolId) {
        return toolJpaRepository.isToolRemoved(toolId);
    }

    @Override public List<Tool> findAllByReservationHasToolsIn(final List<ReservationHasTool> reservationHasTools) {
        return toolJpaRepository.findAllByReservationHasToolsIn(reservationHasTools);
    }

    @Override public List<Tool> findAll() {
        return toolJpaRepository.findAll();
    }

    @Override public Page<Tool> findAll(final boolean removed, final Pageable pageable) {
        return toolJpaRepository.findAll(removed, pageable);
    }

    @Override public Page<Tool> findAllWithLimit(final Pageable pageable) {
        return toolJpaRepository.findAllWithLimit(pageable);
    }

    @Override public Optional<Tool> findById(final Long toolId) {
        return toolJpaRepository.findById(toolId);
    }

    @Override public Tool save(final Tool tool) {
        return toolJpaRepository.save(tool);
    }

}
