package com.s1gawron.rentalservice.tool.repository;

import com.s1gawron.rentalservice.tool.model.ToolState;

public interface ToolStateDAO {

    ToolState save(final ToolState toolState);

    void deleteAll();

}
