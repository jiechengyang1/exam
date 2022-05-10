package com.jcy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcy.entity.QuestionBank;
import com.jcy.vo.BankHaveQuestionSum;
import com.jcy.vo.PageResponse;
import com.jcy.vo.QuestionVo;

import java.util.List;

/**
 * @author JCY
 * @implNote 2022/02/10 9:05
 */
public interface QuestionBankService extends IService<QuestionBank> {

    PageResponse<BankHaveQuestionSum> getBankHaveQuestionSumByType(String bankName, Integer pageNo, Integer pageSize);

    List<QuestionVo> getQuestionsByBankId(Integer bankId);

    List<QuestionVo> getQuestionByBankIdAndType(Integer bankId, Integer type);

    List<QuestionBank> getAllQuestionBanks();

    void addQuestionToBank(String questionIds, String banks);

    void removeBankQuestion(String questionIds, String banks);

    void deleteQuestionBank(String ids);

    void addQuestionBank(QuestionBank questionBank);
}
