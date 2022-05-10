package com.jcy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcy.annotation.Cache;
import com.jcy.entity.Answer;
import com.jcy.entity.Question;
import com.jcy.entity.QuestionBank;
import com.jcy.mapper.AnswerMapper;
import com.jcy.mapper.QuestionBankMapper;
import com.jcy.mapper.QuestionMapper;
import com.jcy.service.QuestionBankService;
import com.jcy.utils.RedisUtil;
import com.jcy.vo.BankHaveQuestionSum;
import com.jcy.vo.PageResponse;
import com.jcy.vo.QuestionVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jcy.utils.CommonUtils.setLikeWrapper;

/**
 * @author JCY
 * @implNote 2022/02/10 9:05
 */
@Service
@RequiredArgsConstructor
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {

    private final QuestionBankMapper questionBankMapper;

    private final QuestionMapper questionMapper;

    private final AnswerMapper answerMapper;

    private final RedisUtil redisUtil;

    @Override
    public PageResponse<BankHaveQuestionSum> getBankHaveQuestionSumByType(String bankName, Integer pageNo, Integer pageSize) {
        QueryWrapper<QuestionBank> wrapper = new QueryWrapper<>();
        setLikeWrapper(wrapper, Collections.singletonMap("bank_name", bankName));

        IPage<QuestionBank> iPage = questionBankMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        List<QuestionBank> questionBanks = iPage.getRecords();

        // 封装成传给前端的数据类型
        List<BankHaveQuestionSum> bankHaveQuestionSums = new ArrayList<>();
        for (QuestionBank questionBank : questionBanks) {
            // 创建vo对象
            BankHaveQuestionSum bankHaveQuestionSum = new BankHaveQuestionSum();
            // 设置属性
            bankHaveQuestionSum.setQuestionBank(questionBank);
            // 设置单选题的数量
            Integer singleQuestionCount = questionMapper.selectCount(new QueryWrapper<Question>().eq("qu_type", 1).like("qu_bank_name", questionBank.getBankName()));
            bankHaveQuestionSum.setSingleChoice(singleQuestionCount);
            // 设置多选题的数量
            Integer multipleQuestionCount = questionMapper.selectCount(new QueryWrapper<Question>().eq("qu_type", 2).like("qu_bank_name", questionBank.getBankName()));
            bankHaveQuestionSum.setMultipleChoice(multipleQuestionCount);
            // 设置判断题的数量
            Integer judgeQuestionCount = questionMapper.selectCount(new QueryWrapper<Question>().eq("qu_type", 3).like("qu_bank_name", questionBank.getBankName()));
            bankHaveQuestionSum.setJudge(judgeQuestionCount);
            // 设置简答题的数量
            Integer shortAnswerQuestionCount = questionMapper.selectCount(new QueryWrapper<Question>().eq("qu_type", 4).like("qu_bank_name", questionBank.getBankName()));
            bankHaveQuestionSum.setShortAnswer(shortAnswerQuestionCount);
            // 加入list中
            bankHaveQuestionSums.add(bankHaveQuestionSum);
        }
        return PageResponse.<BankHaveQuestionSum>builder().data(bankHaveQuestionSums).total(iPage.getTotal()).build();
    }

    @Cache(prefix = "questionBankQuestion", suffix = "#bankId", ttl = 6, randomTime = 1, timeUnit = TimeUnit.HOURS)
    @Override
    public List<QuestionVo> getQuestionsByBankId(Integer bankId) {
        QuestionBank bank = questionBankMapper.selectById(bankId);
        // 在题库中的(单选,多选,判断题)题目
        List<Question> questions = questionMapper.selectList(new QueryWrapper<Question>().like("qu_bank_name", bank.getBankName()).in("qu_type", 1, 2, 3));
        // 构造前端需要的vo对象
        List<QuestionVo> questionVos = new ArrayList<>();
        for (Question question : questions) {
            QuestionVo questionVo = new QuestionVo();

            questionVo.setQuestionId(question.getId());
            questionVo.setQuestionLevel(question.getLevel());
            if (question.getImage() != null && !question.getImage().equals("")) // 防止没有图片对象
                questionVo.setImages(question.getImage().split(","));
            questionVo.setCreatePerson(question.getCreatePerson());
            questionVo.setAnalysis(question.getAnalysis());
            questionVo.setQuestionContent(question.getQuContent());
            questionVo.setQuestionType(question.getQuType());

            Answer answer = answerMapper.selectOne(new QueryWrapper<Answer>().eq("question_id", question.getId()));
            // 选项个数
            String[] options = answer.getAllOption().split(",");
            String[] images = answer.getImages().split(",");
            // 构造答案对象
            QuestionVo.Answer[] handleAnswer = new QuestionVo.Answer[options.length];
            // 字段处理
            for (int i = 0; i < options.length; i++) {
                QuestionVo.Answer answer1 = new QuestionVo.Answer();
                if (images.length - 1 >= i && images[i] != null && !images[i].equals(""))
                    answer1.setImages(new String[]{images[i]});
                answer1.setAnswer(options[i]);
                answer1.setId(i);
                answer1.setIsTrue("false");
                handleAnswer[i] = answer1;
            }
            if (question.getQuType() != 2) {// 单选和判断
                int trueOption = Integer.parseInt(answer.getTrueOption());
                handleAnswer[trueOption].setIsTrue("true");
                handleAnswer[trueOption].setAnalysis(answer.getAnalysis());
            } else {// 多选
                String[] trueOptions = answer.getTrueOption().split(",");
                for (String trueOption : trueOptions) {
                    handleAnswer[Integer.parseInt(trueOption)].setIsTrue("true");
                    handleAnswer[Integer.parseInt(trueOption)].setAnalysis(answer.getAnalysis());
                }
            }
            questionVo.setAnswer(handleAnswer);
            questionVos.add(questionVo);
        }
        return questionVos;
    }

    @Override
    public List<QuestionVo> getQuestionByBankIdAndType(Integer bankId, Integer type) {
        List<QuestionVo> questionVoList = getQuestionsByBankId(bankId);
        questionVoList.removeIf(questionVo -> !Objects.equals(questionVo.getQuestionType(), type));
        return questionVoList;
    }

    @Cache(prefix = "questionBanks", ttl = 10, timeUnit = TimeUnit.HOURS)
    @Override
    public List<QuestionBank> getAllQuestionBanks() {
        return questionBankMapper.selectList(null);
    }

    @Override
    public void addQuestionToBank(String questionIds, String banks) {
        // 需要操作的问题
        String[] quIds = questionIds.split(",");
        // 需要放入的题库id
        String[] bankIds = banks.split(",");

        // 将每一个题目放入每一个题库中
        for (String quId : quIds) {
            // 当前的问题对象
            Question question = questionMapper.selectById(Integer.parseInt(quId));
            String quBankId = question.getQuBankId();
            // 当前已经有的题库id
            String[] qid = quBankId.split(",");
            System.out.println(quBankId);
            // 存在去重后的题库id
            Set<Integer> allId = new HashSet<>();
            if (!quBankId.equals("")) {// 防止题目没有题库
                for (String s : qid) {
                    allId.add(Integer.parseInt(s));
                }
            }
            // 将新增的仓库id放入
            for (String bankId : bankIds) {
                // remove cache
                redisUtil.del("questionBankQuestion:" + bankId);
                allId.add(Integer.parseInt(bankId));
            }
            // 处理后的id字符串 例如(1,2,3)
            String handleHaveBankIds = allId.toString().replaceAll(" ", "");
            handleHaveBankIds = handleHaveBankIds.substring(1, handleHaveBankIds.length() - 1);
            // 更新当前用户的题库id值
            question.setQuBankId(handleHaveBankIds);

            // 将存放处理后的set集合遍历,然后替换数据库的题库名
            StringBuilder bankNames = new StringBuilder();
            for (Integer id : allId) {
                bankNames.append(questionBankMapper.selectById(id).getBankName()).append(",");
            }
            // 替换原来的仓库名称
            question.setQuBankName(bankNames.substring(0, bankNames.toString().length() - 1));
            questionMapper.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
        }
    }

    @Override
    public void removeBankQuestion(String questionIds, String banks) {
        // 需要操作的问题
        String[] quIds = questionIds.split(",");
        // 需要移除的题库id
        String[] bankIds = banks.split(",");
        // 操作需要移除仓库的问题
        for (String quId : quIds) {
            Question question = questionMapper.selectById(Integer.parseInt(quId));
            String quBankId = question.getQuBankId();
            // 当前问题拥有的仓库id
            String[] curHaveId = quBankId.split(",");
            // 存储处理后的id
            Set<Integer> handleId = new HashSet<>();
            if (!quBankId.equals("")) {
                for (String s : curHaveId) {
                    handleId.add(Integer.parseInt(s));
                }
            }
            // 遍历查询set中是否含有需要删除的仓库id
            for (String bankId : bankIds) {
                // remove cache
                redisUtil.del("questionBankQuestion:" + bankId);
                handleId.remove(Integer.parseInt(bankId));
            }
            // 处理后的id字符串 例如(1,2,3)
            String handleHaveBankIds = handleId.toString().replaceAll(" ", "");
            handleHaveBankIds = handleHaveBankIds.substring(1, handleHaveBankIds.length() - 1);
            // 更新当前用户的题库id值
            question.setQuBankId(handleHaveBankIds);

            if (!handleHaveBankIds.equals("")) {// 删除后还存在剩余的题库
                // 将存放处理后的set集合遍历,然后替换数据库的题库名
                StringBuilder bankNames = new StringBuilder();
                for (Integer id : handleId) {
                    bankNames.append(questionBankMapper.selectById(id).getBankName()).append(",");
                }
                // 替换原来的仓库名称
                question.setQuBankName(bankNames.substring(0, bankNames.toString().length() - 1));
            } else {// 不剩题库了
                question.setQuBankName("");
            }
            // 更新问题对象
            questionMapper.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
        }
    }

    @Transactional
    @Override
    public void deleteQuestionBank(String ids) {
        String[] bankId = ids.split(",");
        for (String s : bankId) {
            // 找到题库
            QuestionBank questionBank = questionBankMapper.selectById(s);
            // 找到与此题库相关的所有问题信息
            List<Question> questions = questionMapper.selectList(new QueryWrapper<Question>().like("qu_bank_name", questionBank.getBankName()));
            // 移除与此题库相关的信息
            for (Question question : questions) {
                String quBankName = question.getQuBankName();
                String quBankId = question.getQuBankId();
                String[] name = quBankName.split(",");
                String[] id = quBankId.split(",");
                // 新的题库名
                String[] newName = new String[name.length - 1];
                // 新的题库id数据
                String[] newId = new String[id.length - 1];

                for (int i = 0, j = 0; i < name.length; i++) {
                    if (!name[i].equals(questionBank.getBankName())) {
                        newName[j] = name[i];
                        j++;
                    }
                }
                for (int i = 0, j = 0; i < id.length; i++) {
                    if (!id[i].equals(String.valueOf(questionBank.getBankId()))) {
                        newId[j] = id[i];
                        j++;
                    }
                }
                String handleName = Arrays.toString(newName).replaceAll(" ", "").replaceAll("]", "").replace("[", "");
                String handleId = Arrays.toString(newId).replaceAll(" ", "").replaceAll("]", "").replace("[", "");
                // 设置删除题库后的新字段
                question.setQuBankName(handleName);
                question.setQuBankId(handleId);
                // 更新题目
                questionMapper.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
                redisUtil.del("questionVo:" + question.getId());
            }
            // 删除题库
            questionBankMapper.deleteById(Integer.parseInt(s));
            // 清楚缓存
            redisUtil.del("questionBankQuestion:" + s);
        }
    }

    @Override
    public void addQuestionBank(QuestionBank questionBank) {
        questionBankMapper.insert(questionBank);
        redisUtil.del("questionBanks");
    }
}
