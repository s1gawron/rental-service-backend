package com.s1gawron.rentalservice.tool.repository;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {

    List<Tool> findAllByToolCategory(final ToolCategory category);

    @Query(value = "SELECT * FROM tool ORDER BY date_added DESC LIMIT 3", nativeQuery = true)
    List<Tool> findNewTools();

    List<Tool> findByNameContainingIgnoreCase(final String toolName);

    @Query(value = "SELECT is_available FROM tool WHERE tool_id = :toolId", nativeQuery = true)
    Optional<Boolean> isToolAvailable(@Param(value = "toolId") final long toolId);

    List<Tool> findAllByReservationHasToolsIn(final List<ReservationHasTool> reservationHasTools);

}
