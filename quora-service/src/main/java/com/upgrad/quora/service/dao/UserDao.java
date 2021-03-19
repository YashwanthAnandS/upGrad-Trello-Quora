package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {
    @PersistenceContext
    EntityManager entityManager;

    //this dao method sign up specific user in the database
    public UserEntity signupUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    //this dao method get username from the database
    public UserEntity getUserByUsername(String username) {
        try {
            return entityManager.createNamedQuery("UserName", UserEntity.class).
                    setParameter("username", username).getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }

    }

    //this dao method get emailId from the database
    public UserEntity getUserByEmail(String email) {
        try {
            return entityManager.createNamedQuery("UserEmail", UserEntity.class).
                    setParameter("email", email).getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }
    }

    //this dao method save user login information in the database
    public UserAuthEntity saveLoginInfo(UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    //this dao method get specific users uuid from the database
    public UserEntity getUserByUuid(String uuid) {
        try {
            return entityManager.createNamedQuery("UserId", UserEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }
    }

}

