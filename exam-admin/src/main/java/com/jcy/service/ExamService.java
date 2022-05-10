package com.jcy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcy.entity.Exam;
import com.jcy.vo.AddExamByBankVo;
import com.jcy.vo.AddExamByQuestionVo;
import com.jcy.vo.ExamQueryVo;
import com.jcy.vo.PageResponse;

import java.util.List;

/**
 * @author JCY
 * @implNote 2022/02/10 9:05
 */
public interface ExamService extends IService<Exam> {

    PageResponse<Exam> getExamPage(ExamQueryVo examQueryVo);

    AddExamByQuestionVo getExamInfoById(Integer examId);

    void operationExam(Integer type, String ids);

    void addExamByBank(AddExamByBankVo addExamByBankVo);

    void addExamByQuestionList(AddExamByQuestionVo addExamByQuestionVo);

    void updateExamInfo(AddExamByQuestionVo addExamByQuestionVo);

    List<String> getExamPassRateEchartData();

    List<String> getExamNumbersEchartData();
}
