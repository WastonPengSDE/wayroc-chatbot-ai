package com.wayroc.wayrocchatbot.model.domain.request;



import lombok.Data;

import java.io.Serializable;

@Data
public class ChartAddRequest implements Serializable {

    private String chartName;

    private String goal;

    private String chartData;


    private String chartType;



    private  static final long serialVersionUID = 1L;

}
