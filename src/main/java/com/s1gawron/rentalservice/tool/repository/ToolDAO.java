package com.s1gawron.rentalservice.tool.repository;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.tool.model.Tool;

import java.util.List;
import java.util.Optional;

public interface ToolDAO {

    List<Tool> findAllByToolCategory(final String toolCategory);

    List<Tool> findAllByToolCategory(final String toolCategory, final boolean removed);

    List<Tool> findNewTools();

    List<Tool> findByName(final String toolName);

    List<Tool> findNotRemovedToolsByName(final String toolName);

    Optional<Boolean> isToolAvailable(final long toolId);

    Optional<Boolean> isToolRemoved(final long toolId);

    List<Tool> findAllByReservationHasToolsIn(final List<ReservationHasTool> reservationHasTools);

    List<Tool> findAll();

    List<Tool> findAll(final boolean removed);

    List<Tool> findAllWithLimit();

    Optional<Tool> findById(final Long toolId);

    Tool save(final Tool tool);

}
