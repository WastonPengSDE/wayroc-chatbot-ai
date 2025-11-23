package com.wayroc.wayrocchatbot.service;

import com.wayroc.wayrocchatbot.model.domain.Chart;
import com.wayroc.wayrocchatbot.model.domain.request.ChartAddRequest;

public interface ChartService {
    Chart addChart(ChartAddRequest chartAddRequest);

    Chart getById(Long id);

    boolean deleteById(Long id);

    Object listCharts(int current, int size);
}
