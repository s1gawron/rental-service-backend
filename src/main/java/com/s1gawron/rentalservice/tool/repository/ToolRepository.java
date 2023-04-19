package com.s1gawron.rentalservice.tool.repository;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.tool.model.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {

    @Query(value = "SELECT * FROM tool WHERE tool_category = :toolCategory LIMIT 500", nativeQuery = true)
    List<Tool> findAllByToolCategory(@Param(value = "toolCategory") final String toolCategory);

    @Query(value = "SELECT * FROM tool WHERE tool_category = :toolCategory AND is_removed = :removed LIMIT 500", nativeQuery = true)
    List<Tool> findAllByToolCategory(@Param(value = "toolCategory") final String toolCategory, @Param(value = "removed") final boolean removed);

    @Query(value = "SELECT * FROM tool WHERE is_removed = false ORDER BY date_added DESC LIMIT 3", nativeQuery = true)
    List<Tool> findNewTools();

    @Query(value = "SELECT * FROM tool WHERE UPPER(name) LIKE UPPER(concat('%', :toolName, '%')) LIMIT 500", nativeQuery = true)
    List<Tool> findByName(final String toolName);

    @Query(value = "SELECT * FROM tool WHERE UPPER(name) LIKE UPPER(concat('%', :toolName, '%')) AND is_removed = FALSE LIMIT 500", nativeQuery = true)
    List<Tool> findNotRemovedToolsByName(@Param(value = "toolName") final String toolName);

    @Query(value = "SELECT is_available FROM tool WHERE tool_id = :toolId", nativeQuery = true)
    Optional<Boolean> isToolAvailable(@Param(value = "toolId") final long toolId);

    List<Tool> findAllByReservationHasToolsIn(final List<ReservationHasTool> reservationHasTools);

    @Query(value = "SELECT * FROM tool WHERE is_removed = :removed LIMIT 500", nativeQuery = true)
    List<Tool> findAll(@Param(value = "removed") final boolean removed);

    @Query(value = "SELECT * FROM tool LIMIT 500", nativeQuery = true)
    List<Tool> findAllWithLimit();

}
