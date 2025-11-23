package com.wayroc.wayrocchatbot.repositoty;

import com.wayroc.wayrocchatbot.model.domain.Chart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {

}
