package com.wayroc.wayrocchatbot.controller;

import com.wayroc.wayrocchatbot.common.BaseResponse;
import com.wayroc.wayrocchatbot.common.DeleteRequest;
import com.wayroc.wayrocchatbot.common.ErrorCode;
import com.wayroc.wayrocchatbot.common.ResultUtils;
import com.wayroc.wayrocchatbot.exception.BusinessException;
import com.wayroc.wayrocchatbot.model.domain.Chart;
import com.wayroc.wayrocchatbot.model.domain.User;
import com.wayroc.wayrocchatbot.model.domain.request.ChartAddRequest;
import com.wayroc.wayrocchatbot.model.domain.request.ChartEditRequest;
import com.wayroc.wayrocchatbot.model.domain.request.GenChartByAiRequest;
import com.wayroc.wayrocchatbot.model.domain.vo.BiResponse;
import com.wayroc.wayrocchatbot.service.ChartService;
import com.wayroc.wayrocchatbot.service.UserService;
import com.wayroc.wayrocchatbot.utils.ExcelUtils;
//import com.wayroc.wayrocchatbot.manager.AiManager;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

//    @Resource
//    private AiManager aiManager;

    /**
     * 1. 新增图表
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest) {
        if (chartAddRequest == null) {
            // 这里沿用你自己的 ErrorCode 写法
            throw new BusinessException(ErrorCode.PARMAS_ERROR);
        }
        Chart saved = chartService.addChart(chartAddRequest);
        return ResultUtils.success(saved.getId());
    }

    /**
     * 2. 查询图表（根据 id）
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChart(@RequestParam("id") Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "id 非法");
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.Null_ERROR, "图表不存在");
        }
        return ResultUtils.success(chart);
    }

    /**
     * 3. 删除图表（逻辑删除）
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "删除参数错误");
        }
        boolean result = chartService.deleteById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 4. 简单分页查询（current 从 1 开始）
     */
    @GetMapping("/list")
    public BaseResponse<?> listCharts(@RequestParam int current,
                                      @RequestParam int size) {
        if (current <= 0 || size <= 0) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "分页参数错误");
        }
        return ResultUtils.success(chartService.listCharts(current, size));
    }

    /**
     * 4. 简单分页查询（current 从 1 开始）
     *
     *（file , req(name,goal) , httpServletrequest()）
     *1. 取参
     *  name
     *  goal
     *  chart type
     *
     *
     * 校验 (is blank length)
     *
     * 2 文件大小 文件后缀
     *
     * 3 鉴权 //tbd
     *
     * 4 限流 //
     *
     * 5 excel util
     *
     * 6. prompt
     *
     * 7. save chart // 可选项 所有 chart 增删改查
     *
     * 8. return Bi response
     *
     *
     *
     *
     */

}