package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    //This Dao method creates a question in database
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    //This Dao method fetch all the questions from the database
    public List<QuestionEntity> getAllQuestions(final String accessToken) {
        return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
    }

    //This Dao method fetch question from their questionId from the database
    public QuestionEntity getQuestionById(final String uuid) {
        try {
            return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //This Dao method updates the question content in the database
    public QuestionEntity editQuestionContent(QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
        return questionEntity;
    }

    //This DAO method deletes a question in the database
    public QuestionEntity deleteSelectedQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
        return questionEntity;
    }

    //This DAO method fetches all the questions as per the user id
    public List<QuestionEntity> getAllQuestionsByUser(final String accessToken, final String userId) {
        return entityManager.createNamedQuery("allQuestionsByUserId", QuestionEntity.class).setParameter("user_id", userId).getResultList();
    }
}


