package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AuthTokenDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private AuthTokenDao authTokenDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    //This service class method creates a question in 'quora' database by all users
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthTokenEntity = authTokenDao.checkAuthToken(accessToken);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutTime() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        questionEntity.setUser(userAuthTokenEntity.getUser());
        return questionDao.createQuestion(questionEntity);
    }

    //This service class methods fetches all the questions from the database which is created by all the users
    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(final String accessToken) throws AuthorizationFailedException {

        UserAuthEntity userAuthTokenEntity = authTokenDao.checkAuthToken(accessToken);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutTime() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }

        return questionDao.getAllQuestions(accessToken);
    }

    //This service class method updates the question content by only owner of that question
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(final String content, final String questionId, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthTokenEntity = authTokenDao.checkAuthToken(accessToken);

        QuestionEntity questionEntity = questionDao.getQuestionById(questionId);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutTime() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if (questionEntity.getUser().getUUID() != userAuthTokenEntity.getUser().getUUID()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        questionEntity.setContent(content);

        return questionDao.editQuestionContent(questionEntity);
    }

    //This service class method is for deleting the question either by the admin or the user owning it
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(final String questionId, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthTokenEntity = authTokenDao.checkAuthToken(accessToken);
        QuestionEntity questionEntity = questionDao.getQuestionById(questionId);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutTime() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if (questionEntity.getUser().getUUID() == userAuthTokenEntity.getUser().getUUID() || questionEntity.getUser().getRole() == "admin") {
            return  questionDao.deleteSelectedQuestion(questionEntity);

        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

    }

    //This is service class method for fetching questions as per user id
    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsByUser(final String userId, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuthTokenEntity = authTokenDao.checkAuthToken(accessToken);
        UserEntity userEntity = userDao.getUserByUuid(userId);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutTime() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.getAllQuestionsByUser(accessToken, userId);
    }
}
