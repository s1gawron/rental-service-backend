package com.s1gawron.rentalservice.tool.repository.impl;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.repository.ToolDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaToolDAO implements ToolDAO {

    private final ToolJpaRepository toolJpaRepository;

    public JpaToolDAO(final ToolJpaRepository toolJpaRepository) {
        this.toolJpaRepository = toolJpaRepository;
    }

    @Override public List<Tool> findAllByToolCategory(final String toolCategory) {
        return toolJpaRepository.findAllByToolCategory(toolCategory);
    }

    @Override public List<Tool> findAllByToolCategory(final String toolCategory, final boolean removed) {
        return toolJpaRepository.findAllByToolCategory(toolCategory, removed);
    }

    @Override public List<Tool> findNewTools() {
        return toolJpaRepository.findNewTools();
    }

    @Override public List<Tool> findByName(final String toolName) {
        return toolJpaRepository.findByName(toolName);
    }

    @Override public List<Tool> findNotRemovedToolsByName(final String toolName) {
        return toolJpaRepository.findNotRemovedToolsByName(toolName);
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

    @Override public List<Tool> findAll(final boolean removed) {
        return toolJpaRepository.findAll(removed);
    }

    @Override public List<Tool> findAllWithLimit() {
        return toolJpaRepository.findAllWithLimit();
    }

    @Override public Optional<Tool> findById(final Long toolId) {
        return toolJpaRepository.findById(toolId);
    }

    @Override public Tool save(final Tool tool) {
        return toolJpaRepository.save(tool);
    }

}
