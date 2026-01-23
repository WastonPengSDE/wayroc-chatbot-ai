package com.wayroc.wayrocchatbot.service;

import com.wayroc.wayrocchatbot.common.ErrorCode;
import com.wayroc.wayrocchatbot.exception.BusinessException;
import com.wayroc.wayrocchatbot.model.domain.Chart;
import com.wayroc.wayrocchatbot.model.domain.request.ChartAddRequest;
import com.wayroc.wayrocchatbot.repositoty.ChartRepository;
import com.wayroc.wayrocchatbot.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ChartServiceImpl implements ChartService {
    @Autowired
    private ChartRepository ChartRepository;

    @Override
    public Chart addChart(ChartAddRequest chartAddRequest) {
        //错误处理没写

        Chart chart = new Chart();
        chart.setName(chartAddRequest.getChartName());
        chart.setGoal(chartAddRequest.getGoal());
        chart.setChartData(chartAddRequest.getChartData());
        chart.setChartType(chartAddRequest.getChartType());
        return ChartRepository.save(chart);
    }

    @Override
    public Chart getById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "Invalid id");
        }

        Optional<Chart> ChartOpt = ChartRepository.findById(id);
        if (ChartOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "the check user password does not exist");
        }

        return ChartOpt.get();
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "Invalid id");
        }
        //id不存在
        if (!ChartRepository.existsById(id)) {
            return false;
        }
        //存在即删除
        ChartRepository.deleteById(id);
        return true;
    }

    @Override
    public Object listCharts(int current, int size) {
        if (current <= 0 || size <= 0) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "Invalid current or size");
        }
        PageRequest pageRequest = PageRequest.of(current, size);
        return ChartRepository.findAll(pageRequest);
    }

    @Override
    public Chart save(Chart chart){
        if (chart == null) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "chart 不能为空");
        }

        chart.setCreateTime(new Date());
        chart.setUpdateTime(new Date());
        return ChartRepository.save(chart);
    }
}
