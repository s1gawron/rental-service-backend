package com.s1gawron.rentalservice.tool.repository;

import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {

    List<Tool> findAllByToolCategory(final ToolCategory category);

    @Query(value = "SELECT * FROM Tool ORDER BY date_added DESC LIMIT 3", nativeQuery = true)
    List<Tool> findNewTools();

    List<Tool> findByNameContainingIgnoreCase(final String toolName);

}
