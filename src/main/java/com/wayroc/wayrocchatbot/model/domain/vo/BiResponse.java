package com.wayroc.wayrocchatbot.model.domain.vo;

import lombok.Data;

/**
 * 智能分析返回给前端的对象（先凑合一个最简单的）
 */
@Data
public class BiResponse {

    /**
     * 生成的图表配置（Echarts option 的 JSON 字符串）
     */
    private String genChart;

    /**
     * 生成的结论文本
     */
    private String genResult;

    /**
     * 图表 id（如果以后要存数据库可以用，现在先留着，可为 null）
     */
    private Long chartId;
}
