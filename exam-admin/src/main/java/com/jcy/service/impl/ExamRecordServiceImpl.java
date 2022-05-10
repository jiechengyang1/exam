package com.jcy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcy.annotation.Cache;
import com.jcy.entity.Answer;
import com.jcy.entity.ExamQuestion;
import com.jcy.entity.ExamRecord;
import com.jcy.entity.User;
import com.jcy.exception.BusinessException;
import com.jcy.exception.CommonErrorCode;
import com.jcy.mapper.AnswerMapper;
import com.jcy.mapper.ExamQuestionMapper;
import com.jcy.mapper.ExamRecordMapper;
import com.jcy.mapper.UserMapper;
import com.jcy.service.ExamRecordService;
import com.jcy.utils.CertificateUtil.ContentStyle;
import com.jcy.utils.CertificateUtil.DateTimeUtil;
import com.jcy.utils.CertificateUtil.PDFUtil;
import com.jcy.utils.JwtUtils;
import com.jcy.utils.RedisUtil;
import com.jcy.utils.SaltEncryption;
import com.jcy.vo.PageResponse;
import com.jcy.vo.TokenVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jcy.utils.CommonUtils.setEqualsQueryWrapper;

/**
 * @Date 2022/02/10 9:05
 * @created JCY
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    private final UserMapper userMapper;

    private final ExamRecordMapper examRecordMapper;

    private final ExamQuestionMapper examQuestionMapper;

    private final AnswerMapper answerMapper;

    private final RedisUtil redisUtil;

    @Override
    public PageResponse<ExamRecord> getUserGrade(String username, Integer examId, Integer pageNo, Integer pageSize) {
        User user = Optional.ofNullable(userMapper.selectOne(new QueryWrapper<User>().eq("username", username))).orElseThrow(() -> new BusinessException(CommonErrorCode.E_100102));

        QueryWrapper<ExamRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user.getId());
        setEqualsQueryWrapper(wrapper, Collections.singletonMap("exam_id", examId));

        IPage<ExamRecord> page = examRecordMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);

        return PageResponse.<ExamRecord>builder().data(page.getRecords()).total(page.getTotal()).build();
    }

    @Cache(prefix = "exam:record", suffix = "#recordId", ttl = 10, randomTime = 2, timeUnit = TimeUnit.HOURS)
    @Override
    public ExamRecord getExamRecordById(Integer recordId) {
        return examRecordMapper.selectById(recordId);
    }

    @Override
    public void createExamCertificate(HttpServletResponse response, String examName, Integer examRecordId) {
        //  1. 查询考试记录信息
        ExamRecord examRecord = getExamRecordById(examRecordId);
        //  2. 查询用户的真实姓名生成证书
        User user = Optional.ofNullable(userMapper.selectById(examRecord.getUserId())).orElse(User.builder().trueName("该用户已注销").build());

        //  ****windows下用如下路径****
        //  获取证书背景图片路径
        String backgroundImage = Objects.requireNonNull(PDFUtil.class.getClassLoader().getResource("static/images/certificateBg.png")).getPath();
        //  获取发放证书的项目Logo
        String logo = Objects.requireNonNull(PDFUtil.class.getClassLoader().getResource("static/images/logo.png")).getPath();
        //  生成的pdf的文件位置(一个模板多次生成)
        String pdfFilePath = Objects.requireNonNull(PDFUtil.class.getClassLoader().getResource("static/templateCertificate.pdf")).getPath();


        //  生成工具类
        PDFUtil pdfUtil = new PDFUtil();

        //  证书字体样式
        ContentStyle style1 = new ContentStyle();
        style1.setFontSize(15);
        ContentStyle style2 = new ContentStyle();
        style2.setFontSize(10);

        //  准备证书所需要的数据
        String trueName = user.getTrueName();
        Date examTime = examRecord.getExamTime();
        //  生成XXX同学信息
        String userInfo = trueName + "同学：";
        //  生成证书内容
        String content = "您于" + DateTimeUtil.DateToString(examTime) + "在" + examName + "测评中取得优异成绩!";
        //  创建证书
        try {
            pdfUtil.openDocument(pdfFilePath).addImage(backgroundImage, 0, 400).addLogo(logo, 270, 480).addContent(userInfo, 85, 630, style1).addContent("特发此证,以资鼓励!", 125, 495, style2).addContent("Power By JieChengYang", 360, 495, style2);
            //  结束截取字符串的索引
            int end;
            //  证书内容分行,防止超出证书边缘
            for (int i = 0, y = 590; i < content.length(); y -= 30) {
                end = Math.min(i + 30, content.length());
                pdfUtil.addContent(content.substring(i, end), 125, y, style1);
                i = end;
            }
        } catch (Exception e) {
            log.error("生成证书错误: " + e);
        }
        //  关闭创建pdf的工具
        pdfUtil.close();
        //  文件转码
        if (pdfFilePath.contains("%")) {
            try {
                pdfFilePath = URLDecoder.decode(pdfFilePath, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //  输出流
        ServletOutputStream out = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(pdfFilePath);
            String[] dir = pdfFilePath.split("/");
            //  获取文件名
            String fileName = dir[dir.length - 1];
            String[] array = fileName.split("[.]");
            //  文件类型
            String fileType = array[array.length - 1].toLowerCase();
            // 设置文件ContentType类型
            if ("jpg,jepg,gif,png".contains(fileType)) {// 图片类型
                response.setContentType("image/" + fileType);
            } else if ("pdf".contains(fileType)) {// pdf类型
                response.setContentType("application/pdf");
            } else {// 自动判断下载文件类型
                response.setContentType("multipart/form-data");
            }
            // 设置文件头：最后一个参数是设置下载文件名
            // response.setHeader("Content-Disposition", "attachment;fileName="+fileName);
            out = response.getOutputStream();
            //  读取文件流
            int len;
            byte[] buffer = new byte[1024 * 10];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            log.error("responseFileStream error:FileNotFoundException" + e);
        } catch (Exception e) {
            log.error("responseFileStream error:" + e);
        } finally {
            try {
                assert out != null;
                out.close();
                in.close();
            } catch (NullPointerException e) {
                log.error("responseFileStream stream close() error:NullPointerException" + e);
            } catch (Exception e) {
                log.error("responseFileStream stream close() error:" + e);
            }
        }
    }

    @Override
    public Integer addExamRecord(ExamRecord examRecord, HttpServletRequest request) {
        // 当前用户对象的信息
        TokenVo tokenVo = JwtUtils.getUserInfoByToken(request);
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));
        // 设置考试信息的字段
        examRecord.setUserId(user.getId());
        // 设置id
        List<ExamRecord> examRecords = examRecordMapper.selectList(null);
        int id = 1;
        if (examRecords.size() > 0) {
            id = examRecords.get(examRecords.size() - 1).getRecordId() + 1;
        }
        examRecord.setRecordId(id);

        // 设置逻辑题目的分数
        // 查询所有的题目答案信息
        List<Answer> answers = answerMapper.selectList(new QueryWrapper<Answer>().in("question_id", Arrays.asList(examRecord.getQuestionIds().split(","))));
        // 查询考试的题目的分数
        HashMap<String, String> map = new HashMap<>();// key是题目的id  value是题目分值
        ExamQuestion examQuestion = examQuestionMapper.selectOne(new QueryWrapper<ExamQuestion>().eq("exam_id", examRecord.getExamId()));
        // 题目的id
        String[] ids = examQuestion.getQuestionIds().split(",");
        // 题目在考试中对应的分数
        String[] scores = examQuestion.getScores().split(",");
        for (int i = 0; i < ids.length; i++) {
            map.put(ids[i], scores[i]);
        }
        // 逻辑分数
        int logicScore = 0;
        // 错题的id
        StringBuilder sf = new StringBuilder();
        // 用户的答案
        String[] userAnswers = examRecord.getUserAnswers().split("-");
        for (int i = 0; i < examRecord.getQuestionIds().split(",").length; i++) {
            int index = SaltEncryption.getIndex(answers, Integer.parseInt(examRecord.getQuestionIds().split(",")[i]));
            if (index != -1) {
                if (Objects.equals(userAnswers[i], answers.get(index).getTrueOption())) {
                    logicScore += Integer.parseInt(map.get(examRecord.getQuestionIds().split(",")[i]));
                } else {
                    sf.append(examRecord.getQuestionIds().split(",")[i]).append(",");
                }
            }
        }
        examRecord.setLogicScore(logicScore);
        if (sf.length() > 0) {// 存在错的逻辑题
            examRecord.setErrorQuestionIds(sf.substring(0, sf.toString().length() - 1));
        }

        System.out.println(examRecord);
        examRecord.setExamTime(new Date());
        examRecordMapper.insert(examRecord);
        return id;
    }

    @Override
    public PageResponse<ExamRecord> getExamRecord(Integer examId, Integer pageNo, Integer pageSize) {
        QueryWrapper<ExamRecord> wrapper = new QueryWrapper<>();
        setEqualsQueryWrapper(wrapper, Collections.singletonMap("exam_id", examId));

        IPage<ExamRecord> page = examRecordMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);

        return PageResponse.<ExamRecord>builder().data(page.getRecords()).total(page.getTotal()).build();
    }

    @Override
    public void setObjectQuestionScore(Integer totalScore, Integer examRecordId) {
        ExamRecord examRecord = examRecordMapper.selectOne(new QueryWrapper<ExamRecord>().eq("record_id", examRecordId));
        examRecord.setTotalScore(totalScore);
        examRecordMapper.update(examRecord, new UpdateWrapper<ExamRecord>().eq("record_id", examRecordId));
        redisUtil.del("exam:record:" + examRecordId);
    }
}
