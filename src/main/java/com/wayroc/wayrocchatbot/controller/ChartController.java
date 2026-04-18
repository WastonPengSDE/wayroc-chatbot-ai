package com.wayroc.wayrocchatbot.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import com.wayroc.wayrocchatbot.common.BaseResponse;
import com.wayroc.wayrocchatbot.common.DeleteRequest;
import com.wayroc.wayrocchatbot.common.ErrorCode;
import com.wayroc.wayrocchatbot.common.ResultUtils;
import com.wayroc.wayrocchatbot.exception.BusinessException;
import com.wayroc.wayrocchatbot.manager.AiManager;
import com.wayroc.wayrocchatbot.model.domain.Chart;
import com.wayroc.wayrocchatbot.model.domain.User;
import com.wayroc.wayrocchatbot.model.domain.request.ChartAddRequest;
import com.wayroc.wayrocchatbot.model.domain.request.ChartEditRequest;
import com.wayroc.wayrocchatbot.model.domain.request.GenChartByAiRequest;
import com.wayroc.wayrocchatbot.model.domain.vo.BiResponse;
import com.wayroc.wayrocchatbot.service.ChartService;
import com.wayroc.wayrocchatbot.service.UserService;
import com.wayroc.wayrocchatbot.utils.ExcelUtils;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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

    @Resource
    private AiManager aiManager;

    @Value("${wayroc.ai.rate-limit-per-second:10}")
    private double genChartRateLimitPermitsPerSecond;

    private volatile RateLimiter genChartRateLimiter;

    /**
     * 1. 新增图表
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest) {
        if (chartAddRequest == null) {
            // 这里沿用你自己的 ErrorCode 写法
            throw new BusinessException(ErrorCode.PARAM_ERROR);
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
            throw new BusinessException(ErrorCode.PARAM_ERROR, "id 非法");
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
            throw new BusinessException(ErrorCode.PARAM_ERROR, "删除参数错误");
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
            throw new BusinessException(ErrorCode.PARAM_ERROR, "分页参数错误");
        }
        return ResultUtils.success(chartService.listCharts(current, size));
    }

    /**
     * 5.
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
    @PostMapping(
            value = "/genchart",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public BaseResponse<BiResponse> genChart(
            @RequestPart("file") MultipartFile file,
            @RequestPart("req") String reqJson
//            ,HttpServletRequest request
    ) throws Exception{
        // 这里写你之前那 1–8 步逻辑
        //取參
        GenChartByAiRequest req =
                new ObjectMapper().readValue(reqJson, GenChartByAiRequest.class);
        String name=req.getName();
        String goal=req.getGoal();
        String chartType=req.getChartType();
        String finalgoal=String.format(
                "Chart name: %s. Analysis goal: %s. Preferred chart type: %s.",
                name,
                goal,
                chartType
        );

        //校驗文件
        //文件是否為空
        if (file==null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件爲空，請重新上傳");
        }

        //文件大小校驗
        long maxSize = 5*1024*1024;
        if (file.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件過大，僅支持5MB以下的文件");
        }

        //文件名與後綴校驗
        String filename=file.getOriginalFilename();
        if (filename==null || !(filename.toLowerCase().endsWith(".xls")||filename.toLowerCase().endsWith(".xlsx")||filename.toLowerCase().endsWith(".csv"))){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件類型錯誤，請上傳csv文件");
        }

        //excel util
        String csv = ExcelUtils.excelToCsv(file);

        RateLimiter limiter = resolveGenChartRateLimiter();
        if (limiter != null && !limiter.tryAcquire()) {
            throw new BusinessException(ErrorCode.RATE_LIMIT);
        }

        //AI manager（使用注入的 Bean，不再 new）
        String out = aiManager.generateEchartsOptionJson(
                finalgoal,
                csv
        );

        //新增錯誤處理
        if (out==null || out.isBlank()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI返回爲空");
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(out);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI返回内容不是合法json");
        }

        if (!root.hasNonNull("echartsCode") || !root.hasNonNull("summary")) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI返回json格式不合要求");
        }
        String echartsCode = root.get("echartsCode").asText();
        String summary = root.get("summary").asText();

        //saveChart?目前缺少字段userId
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartType(chartType);
        chart.setChartData(csv);
        chart.setGenChart(echartsCode);
        chart.setGenResult(summary);
        chartService.save(chart);

        //構建返回BiResponse
        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(echartsCode);
        biResponse.setGenResult(summary);

        if (chart!=null && chart.getId()!=null) {
            biResponse.setChartId(chart.getId());
        }

        return ResultUtils.success(biResponse);
    }

    /** {@code genChartRateLimitPermitsPerSecond <= 0} 时关闭限流。 */
    private RateLimiter resolveGenChartRateLimiter() {
        if (genChartRateLimitPermitsPerSecond <= 0) {
            return null;
        }
        RateLimiter local = genChartRateLimiter;
        if (local == null) {
            synchronized (this) {
                local = genChartRateLimiter;
                if (local == null) {
                    genChartRateLimiter = local = RateLimiter.create(genChartRateLimitPermitsPerSecond);
                }
            }
        }
        return local;
    }

}