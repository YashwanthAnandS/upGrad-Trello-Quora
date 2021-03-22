package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AuthTokenDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerBusinessService {

    @Autowired
    private AuthTokenDao authTokenDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AnswerDao answerDao;

    //This service method creates answer for the entered questionId
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answerEntity, final String accessToken, final String questionId) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthTokenEntity = authTokenDao.checkAuthToken(accessToken);
        QuestionEntity questionEntity = questionDao.getQuestionById(questionId);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutTime() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        UserEntity userEntity = userDao.getUserByUuid(userAuthTokenEntity.getUser().getUUID());

        answerEntity.setQuestion(questionEntity);
        answerEntity.setUser(userEntity);
        return answerDao.createAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(String answer, String answerId, String accessToken) throws AuthorizationFailedException,InvalidAnswerException {

        UserAuthEntity userAuthTokenEntity = authTokenDao.checkAuthToken(accessToken);

        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if (userAuthTokenEntity.getLogoutTime() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }

        if (answerEntity == null) {
            throw new InvalidAnswerException("ANS-001", "Entered answer uuid does not exist");
        }

        if (answerEntity.getUser().getUUID() != userAuthTokenEntity.getUser().getUUID()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        answerEntity.setAns(answer);

        return answerDao.editAnswer(answerEntity);

    }

    public AnswerEntity deleteAnswer(String answerId, String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthTokenEntity = authTokenDao.checkAuthToken(accessToken);
        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutTime() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }

        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        if (answerEntity.getUser().getUUID() == userAuthTokenEntity.getUser().getUUID() || answerEntity.getUser().getRole() == "admin") {
            return  answerDao.deleteSelectedAnswer(answerEntity);

        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAllAnswersByQuestion(String questionId, String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthTokenEntity = authTokenDao.checkAuthToken(accessToken);
        QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
        UserEntity userEntity = questionEntity.getUser();

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutTime() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        }

        if (userEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }
        return answerDao.getAllAnswersByQuestion(accessToken, questionId);
    }

}
