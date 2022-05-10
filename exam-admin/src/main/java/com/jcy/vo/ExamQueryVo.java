package com.jcy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author xiaodai
 * @data 2022/2/25 - 21:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamQueryVo {

    private Integer examType;

    private String startTime;

    private String endTime;

    private String examName;

    @NotNull
    private Integer pageNo;

    @NotNull
    private Integer pageSize;

}
