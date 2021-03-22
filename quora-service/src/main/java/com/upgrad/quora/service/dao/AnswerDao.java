package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    //This Dao method creates an answer in database
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    //Dao method fetches answer entity by AnswerID
    public AnswerEntity getAnswerById(final String uuid) {
        try {
            return entityManager.createNamedQuery("answerByUuid", AnswerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //Dao method edits and update answers in database
    public AnswerEntity editAnswer(AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    //Dao method deletes and removes answer from database for a question via answer ID
    public AnswerEntity deleteSelectedAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
        return answerEntity;
    }

    //Dao method fetches all answers by QuestionID
    public List<AnswerEntity> getAllAnswersByQuestion(String accessToken,final String uuid) {
        return entityManager.createNamedQuery("allAnswersByQuestion", AnswerEntity.class).setParameter("uuid", uuid).getResultList();
    }
}
