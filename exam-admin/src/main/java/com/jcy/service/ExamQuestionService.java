package com.jcy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcy.entity.ExamQuestion;

/**
 * @author JCY
 * @implNote 2022/02/10 9:05
 */
public interface ExamQuestionService extends IService<ExamQuestion> {

    ExamQuestion getExamQuestionByExamId(Integer examId);

}
