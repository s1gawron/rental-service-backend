package com.s1gawron.rentalservice.tool.repository;

import com.s1gawron.rentalservice.tool.model.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {

}
