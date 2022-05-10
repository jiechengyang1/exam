package com.jcy.controller;

import com.jcy.entity.ExamQuestion;
import com.jcy.entity.ExamRecord;
import com.jcy.entity.Question;
import com.jcy.entity.QuestionBank;
import com.jcy.service.*;
import com.jcy.utils.OSSUtil;
import com.jcy.utils.RedisUtil;
import com.jcy.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @author JCY
 * @implNote 2022/01/26 15:42
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Api(tags = "老师权限相关的接口")
@RequestMapping(value = "/teacher")
public class TeacherController {

    private final ExamService examService;

    private final UserService userService;

    private final QuestionService questionService;

    private final ExamQuestionService examQuestionService;

    private final ExamRecordService examRecordService;

    private final QuestionBankService questionBankService;

    private final AnswerService answerService;

    private final RedisUtil redisUtil;

    @GetMapping("/getQuestionBank")
    @ApiOperation("获取所有题库信息")
    public CommonResult<List<QuestionBank>> getQuestionBank() {
        return CommonResult.<List<QuestionBank>>builder()
                .data(questionBankService.getAllQuestionBanks())
                .build();
    }

    @GetMapping("/getQuestion")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionType", value = "问题类型", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "questionBank", value = "问题所属题库", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "questionContent", value = "问题内容", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "页面数", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataType = "int", paramType = "query")
    })
    @ApiOperation("获取题目信息,可分页 ----> 查询条件(可无)(questionType,questionBank,questionContent),必须有的(pageNo,pageSize)")
    public CommonResult<PageResponse<Question>> getQuestion(@RequestParam(required = false) String questionType,
                                                            @RequestParam(required = false) String questionBank,
                                                            @RequestParam(required = false) String questionContent,
                                                            Integer pageNo, Integer pageSize) {
        return CommonResult.<PageResponse<Question>>builder()
                .data(questionService.getQuestion(questionType, questionBank, questionContent, pageNo, pageSize))
                .build();
    }

    @GetMapping("/deleteQuestion")
    @ApiOperation("根据id批量删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionIds", value = "问题id的字符串以逗号分隔", required = true, dataType = "string", paramType = "query")
    })
    public CommonResult<Void> deleteQuestion(String questionIds) {
        questionService.deleteQuestionByIds(questionIds);
        return CommonResult.<Void>builder()
                .build();
    }

    @GetMapping("/addBankQuestion")
    @ApiOperation("将问题加入题库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionIds", value = "问题id的字符串以逗号分隔", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "banks", value = "题库id的字符串以逗号分隔", required = true, dataType = "string", paramType = "query")
    })
    public CommonResult<String> addBankQuestion(String questionIds, String banks) {
        questionBankService.addQuestionToBank(questionIds, banks);
        return CommonResult.<String>builder()
                .build();
    }

    @GetMapping("/removeBankQuestion")
    @ApiOperation("将问题从题库移除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionIds", value = "问题id的字符串以逗号分隔", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "banks", value = "题库id的字符串以逗号分隔", required = true, dataType = "string", paramType = "query")
    })
    public CommonResult<Void> removeBankQuestion(String questionIds, String banks) {
        questionBankService.removeBankQuestion(questionIds, banks);
        return CommonResult.<Void>builder()
                .build();
    }

    @PostMapping("/uploadQuestionImage")
    @ApiOperation("接受前端上传的图片,返回上传图片地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "图片文件", required = true, dataType = "file", paramType = "body")
    })
    public CommonResult<String> uploadQuestionImage(MultipartFile file) throws Exception {
        log.info("开始上传文件: {}", file.getOriginalFilename());
        return CommonResult.<String>builder()
                .data(OSSUtil.picOSS(file))
                .message("上传成功")
                .build();
    }

    @PostMapping("/addQuestion")
    @ApiOperation("添加试题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionVo", value = "问题的vo视图对象", required = true, dataType = "questionVo", paramType = "body")
    })
    public CommonResult<Void> addQuestion(@RequestBody @Valid QuestionVo questionVo) {
        questionService.addQuestion(questionVo);
        return CommonResult.<Void>builder().build();
    }

    // TODO 优化成多id一次查询
    @GetMapping("/getQuestionById/{id}")
    @ApiOperation("根据id获取题目信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "问题id", required = true, dataType = "int", paramType = "path")
    })
    public CommonResult<QuestionVo> getQuestionById(@PathVariable("id") Integer id) {
        return CommonResult.<QuestionVo>builder()
                .data(questionService.getQuestionVoById(id))
                .build();
    }

    @PostMapping("/updateQuestion")
    @ApiOperation("更新试题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionVo", value = "问题的vo视图对象", required = true, dataType = "questionVo", paramType = "body")
    })
    public CommonResult<Void> updateQuestion(@RequestBody @Valid QuestionVo questionVo) {
        questionService.updateQuestion(questionVo);
        return CommonResult.<Void>builder()
                .build();
    }

    @GetMapping("/deleteQuestionBank")
    @ApiOperation("删除题库并去除所有题目中的包含此题库的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "删除题库的id字符串逗号分隔", required = true, dataType = "string", paramType = "query")
    })
    public CommonResult<Void> deleteQuestionBank(String ids) {
        questionBankService.deleteQuestionBank(ids);
        return CommonResult.<Void>builder()
                .build();
    }

    @PostMapping("/addQuestionBank")
    @ApiOperation("添加题库信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionBank", value = "题库的实体对象", required = true, dataType = "questionBank", paramType = "body")
    })
    public CommonResult<Void> addQuestionBank(@RequestBody QuestionBank questionBank) {
        questionBankService.addQuestionBank(questionBank);
        return CommonResult.<Void>builder()
                .build();
    }

    @GetMapping("/operationExam/{type}")
    @ApiOperation("操作考试的信息表(type 1启用 2禁用 3删除)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "操作类型", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "ids", value = "操作的考试id集合", required = true, dataType = "string", paramType = "query")
    })
    public CommonResult<Void> operationExam(@PathVariable("type") Integer type, String ids) {
        examService.operationExam(type, ids);
        return CommonResult.<Void>builder()
                .build();
    }

    @PostMapping("/addExamByBank")
    @ApiOperation("根据题库添加考试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addExamByBankVo", value = "根据题库添加考试vo对象", required = true, dataType = "addExamByBankVo", paramType = "body")
    })
    public CommonResult<Void> addExamByBank(@RequestBody @Valid AddExamByBankVo addExamByBankVo) {
        examService.addExamByBank(addExamByBankVo);
        return CommonResult.<Void>builder()
                .build();
    }

    @PostMapping("/addExamByQuestionList")
    @ApiOperation("根据题目列表添加考试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addExamByQuestionVo", value = "通过题目列表添加考试的vo对象", required = true, dataType = "addExamByQuestionVo", paramType = "body")
    })
    public CommonResult<Void> addExamByQuestionList(@RequestBody @Valid AddExamByQuestionVo addExamByQuestionVo) {
        examService.addExamByQuestionList(addExamByQuestionVo);
        return CommonResult.<Void>builder()
                .build();
    }

    @PostMapping("/updateExamInfo")
    @ApiOperation("更新考试的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addExamByQuestionVo", value = "通过题目列表添加考试的vo对象", required = true, dataType = "addExamByQuestionVo", paramType = "body")
    })
    public CommonResult<Void> updateExamInfo(@RequestBody AddExamByQuestionVo addExamByQuestionVo) {
        examService.updateExamInfo(addExamByQuestionVo);
        return CommonResult.<Void>builder()
                .build();
    }

    @PostMapping("/addExamRecord")
    @ApiOperation("保存考试记录信息,返回保存记录的id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examRecord", value = "考试记录实体对象", required = true, dataType = "examRecord", paramType = "body")
    })
    public CommonResult<Integer> addExamRecord(@RequestBody ExamRecord examRecord, HttpServletRequest request) {
        return CommonResult.<Integer>builder()
                .data(examRecordService.addExamRecord(examRecord, request))
                .build();
    }

    @GetMapping("/getExamRecordById/{recordId}")
    @ApiOperation("根据考试的记录id查询用户考试的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "recordId", value = "考试记录id", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<ExamRecord> getExamRecordById(@PathVariable Integer recordId) {
        return CommonResult.<ExamRecord>builder()
                .data(examRecordService.getExamRecordById(recordId))
                .build();
    }

    @GetMapping("/getExamQuestionByExamId/{examId}")
    @ApiOperation("根据考试id查询考试中的每一道题目id和分值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examId", value = "考试id", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<ExamQuestion> getExamQuestionByExamId(@PathVariable Integer examId) {
        return CommonResult.<ExamQuestion>builder()
                .data(examQuestionService.getExamQuestionByExamId(examId))
                .build();
    }

    @GetMapping("/getExamRecord")
    @ApiOperation("获取考试记录信息,(pageNo,pageSize)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examId", value = "考试id", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "页面数", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<PageResponse<ExamRecord>> getExamRecord(@RequestParam(required = false) Integer examId, Integer pageNo, Integer pageSize) {
        return CommonResult.<PageResponse<ExamRecord>>builder()
                .data(examRecordService.getExamRecord(examId, pageNo, pageSize))
                .build();
    }

    // TODO 改成ids查询
    @GetMapping("/getUserById/{userId}")
    @ApiOperation("根据用户id查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<UserInfoVo> getUserById(@PathVariable Integer userId) {
        return CommonResult.<UserInfoVo>builder()
                .data(userService.getUserInfoById(userId))
                .build();
    }

    @GetMapping("/setObjectQuestionScore")
    @ApiOperation("设置考试记录的客观题得分,设置总分为逻辑得分+客观题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "totalScore", value = "总成绩", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "examRecordId", value = "考试记录id", required = true, dataType = "int", paramType = "query")
    })
    public CommonResult<Void> setObjectQuestionScore(Integer totalScore, Integer examRecordId) {
        examRecordService.setObjectQuestionScore(totalScore, examRecordId);
        return CommonResult.<Void>builder()
                .build();
    }

    @GetMapping("/getExamPassRate")
    @ApiOperation("提供每一门考试的通过率数据(echarts绘图)")
    public CommonResult<List<String>> getExamPassRate() {
        return CommonResult.<List<String>>builder()
                .data(examService.getExamPassRateEchartData())
                .build();
    }

    @GetMapping("/getExamNumbers")
    @ApiOperation("提供每一门考试的考试次数(echarts绘图)")
    public CommonResult<List<String>> getExamNumbers() {
        return CommonResult.<List<String>>builder()
                .data(examService.getExamNumbersEchartData())
                .build();
    }
}
