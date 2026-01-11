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

    /** 1. 新增图表 */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest) {
        if (chartAddRequest == null) {
            // 这里沿用你自己的 ErrorCode 写法
            throw new BusinessException(ErrorCode.PARMAS_ERROR);
        }
        Chart saved = chartService.addChart(chartAddRequest);
        return ResultUtils.success(saved.getId());
    }

    /** 2. 查询图表（根据 id） */
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

    /** 3. 删除图表（逻辑删除） */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "删除参数错误");
        }
        boolean result = chartService.deleteById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /** 4. 简单分页查询（current 从 1 开始） */
    @GetMapping("/list")
    public BaseResponse<?> listCharts(@RequestParam int current,
                                      @RequestParam int size) {
        if (current <= 0 || size <= 0) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "分页参数错误");
        }
        return ResultUtils.success(chartService.listCharts(current, size));
    }

    /**
     * 5. 智能分析（按你那段伪代码的最简单逻辑）
     *
     * 前端用 multipart/form-data 传：
     * - file: 文件
     * - 其他字段：name / goal / chartType
     */
//    @PostMapping("/gen")
//    public BaseResponse<?> genChartByAi(
//            @RequestPart("file") MultipartFile multipartFile,
//            GenChartByAiRequest genChartByAiRequest,
//            HttpServletRequest request) {
//
//        // 1. 取参数
//        String name = genChartByAiRequest.getName();
//        String goal = genChartByAiRequest.getGoal();
//        String chartType = genChartByAiRequest.getChartType();
//
//        // 2. 构造 AI 输入
//        StringBuilder userInput = new StringBuilder();
//        userInput.append("分析需求：\n")
//                .append(goal)
//                .append("\n")
//                .append("原始数据：\n");
//
//        // 3. 文件 -> CSV 字符串
//        String csvData = ExcelUtils.excelToCsv(multipartFile);
//        userInput.append(csvData).append("\n");
//
//
//        long biModelId = 1659171950288818178L;
////        String result = aiManager.doChat(biModelId, userInput.toString());
//
//        // 5. 按约定格式拆分结果
////        String[] splits = result.split("【【【【【");
////        if (splits.length < 3) {
////            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
////        }
////        String genChart = splits[1].trim();
////        String genResult = splits[2].trim();
//
////        // 6. 插入数据库
////        User loginUser = userService.getLoginUser(request);
//
////        Chart chart = new Chart();
////        chart.setName(name);
////        chart.setGoal(goal);
////        chart.setChartData(csvData);   // 注意：是 Data 不是 Date
////        chart.setChartType(chartType);
////        chart.setGenChart(genChart);
////        chart.setGenResult(genResult);
////        chart.setUserId(loginUser.getId());
////
////        boolean saveResult = chartService.save(chart);
////        if (!saveResult) {
////            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表保存失败");
////        }
//
//        // 7. 组装返回
//        BiResponse biResponse = new BiResponse();
//        biResponse.setGenChart(genChart);
//        biResponse.setGenResult(genResult);
//        biResponse.setChartId(chart.getId());
//
//        return ResultUtils.success(biResponse);
//    }

}
