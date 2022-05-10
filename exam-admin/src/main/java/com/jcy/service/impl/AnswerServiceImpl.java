package com.jcy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcy.entity.Answer;
import com.jcy.mapper.AnswerMapper;
import com.jcy.service.AnswerService;
import org.springframework.stereotype.Service;

/**
 * @Date 2022/02/10 9:05
 * @created JCY
 */
@Service
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements AnswerService {
}
