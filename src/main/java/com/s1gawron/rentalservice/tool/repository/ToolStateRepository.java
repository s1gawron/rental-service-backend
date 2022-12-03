package com.s1gawron.rentalservice.tool.repository;

import com.s1gawron.rentalservice.tool.model.ToolState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolStateRepository extends JpaRepository<ToolState, Long> {

}
