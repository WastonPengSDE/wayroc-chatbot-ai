package com.wayroc.wayrocchatbot.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

import java.util.Date;

@Table(name = "chart")
@Data
@Entity

public class Chart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String name;

    @Column(columnDefinition = "text")
    private String goal;

    @Column(columnDefinition = "text")
    private String chartData;

    private String chartType;

    @Column(columnDefinition = "text")
    private String genChart;

    @Column(columnDefinition = "text")
    private String genResult;

    private Long userId;

    @Column(updatable = false, insertable = false)
    private Date createTime;

    @Column(insertable = false, updatable = false)
    private Date updateTime;

    private Integer isDelete;
}
