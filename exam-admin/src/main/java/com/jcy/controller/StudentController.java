package com.jcy.controller;

import com.jcy.entity.ExamRecord;
import com.jcy.service.impl.ExamRecordServiceImpl;
import com.jcy.vo.CommonResult;
import com.jcy.vo.PageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author JCY
 * @implNote 2022/01/27 19:44
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "学生权限相关的接口")
@RequestMapping(value = "/student")
public class StudentController {

    private final ExamRecordServiceImpl examRecordService;

    @GetMapping("/getMyGrade")
    @ApiOperation("获取个人成绩(分页 根据考试名查询)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "系统唯一用户名", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "当前页面数", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "当前页面大小", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "examId", value = "考试唯一id", dataType = "int", paramType = "query")
    })
    public CommonResult<PageResponse<ExamRecord>> getMyGrade(String username, Integer pageNo, Integer pageSize,
                                                             @RequestParam(required = false) Integer examId) {
        return CommonResult.<PageResponse<ExamRecord>>builder()
                .data(examRecordService.getUserGrade(username, examId, pageNo, pageSize))
                .build();
    }

    @GetMapping("/getCertificate")
    @ApiOperation("生成证书接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examName", value = "考试名称", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "examRecordId", value = "考试记录id", required = true, dataType = "int", paramType = "query")
    })
    public void getCertificate(HttpServletResponse response, @RequestParam(name = "examName") String examName,
                               @RequestParam(name = "examRecordId") Integer examRecordId) {
        examRecordService.createExamCertificate(response, examName, examRecordId);
    }

}
