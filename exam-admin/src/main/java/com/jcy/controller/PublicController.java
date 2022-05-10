package com.jcy.controller;

import com.jcy.entity.Exam;
import com.jcy.service.ExamService;
import com.jcy.service.NoticeService;
import com.jcy.service.QuestionBankService;
import com.jcy.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = "三个角色公共的相关接口")
@RequestMapping(value = "/public")
public class PublicController {

    private final NoticeService noticeService;

    private final ExamService examService;

    private final QuestionBankService questionBankService;

    @PostMapping("/getExamInfo")
    @ApiOperation("根据信息查询考试的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examQueryVo", value = "考试信息查询vo对象", required = true, dataType = "examQueryVo", paramType = "body")
    })
    public CommonResult<PageResponse<Exam>> getExamInfo(@RequestBody ExamQueryVo examQueryVo) {
        return CommonResult.<PageResponse<Exam>>builder()
                .data(examService.getExamPage(examQueryVo))
                .build();
    }

    @GetMapping("/getExamInfoById")
    @ApiOperation("根据考试id查询考试的信息和题目列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examId", value = "考试id", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<AddExamByQuestionVo> getExamInfoById(@RequestParam Integer examId) {
        return CommonResult.<AddExamByQuestionVo>builder()
                .data(examService.getExamInfoById(examId))
                .build();
    }

    @GetMapping("/allExamInfo")
    @ApiOperation("查询考试所有信息")
    public CommonResult<List<Exam>> allExamInfo() {
        return CommonResult.<List<Exam>>builder()
                .data(examService.list(null))
                .build();
    }

    @GetMapping("/getBankHaveQuestionSumByType")
    @ApiOperation("获取题库中所有题目类型的数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankName", value = "题库名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "页面数", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataType = "int", paramType = "query"),
    })
    public CommonResult<PageResponse<BankHaveQuestionSum>> getBankHaveQuestionSumByType(@RequestParam(required = false) String bankName,
                                                                                        Integer pageNo, Integer pageSize) {
        return CommonResult.<PageResponse<BankHaveQuestionSum>>builder()
                .data(questionBankService.getBankHaveQuestionSumByType(bankName, pageNo, pageSize))
                .build();
    }

    @GetMapping("/getQuestionByBankIdAndType")
    @ApiOperation("根据题库id和题目类型获取题目信息 type(1单选 2多选 3判断)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankId", value = "题库id", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "题目类型", required = true, dataType = "int", paramType = "query"),
    })
    public CommonResult<List<QuestionVo>> getQuestionByBankIdAndType(Integer bankId, Integer type) {
        return CommonResult.<List<QuestionVo>>builder()
                .data(questionBankService.getQuestionByBankIdAndType(bankId, type))
                .build();
    }

    @GetMapping("/getQuestionByBank")
    @ApiOperation("根据题库获取所有的题目信息(单选,多选,判断题)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankId", value = "题库id", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<List<QuestionVo>> getQuestionByBank(Integer bankId) {
        return CommonResult.<List<QuestionVo>>builder()
                .data(questionBankService.getQuestionsByBankId(bankId))
                .build();
    }

    @GetMapping("/getCurrentNewNotice")
    @ApiOperation("获取当前系统最新的公告")
    public CommonResult<String> getCurrentNewNotice() {
        return CommonResult.<String>builder()
                .data(noticeService.getCurrentNotice())
                .build();
    }
}
