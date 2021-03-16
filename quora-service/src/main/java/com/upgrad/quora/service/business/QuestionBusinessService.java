package com.upgrad.quora.service.business;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionBusinessService {

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String accessToken) {
        return null;
    }
}
