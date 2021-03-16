package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

@Repository
public class AuthTokenDao {

    @PersistenceContext
    EntityManager entityManager;

    //this dao method fetch authentication token from the database
    public UserAuthEntity checkAuthToken(String authToken) {
        try {
            return entityManager.createNamedQuery("CheckAuthToken", UserAuthEntity.class).
                    setParameter("accessToken", authToken).getSingleResult();
        } catch (NoResultException exc) {
            return null;
        }
    }

    //this dao method fetch users uuid from the database
    public UserEntity getUserByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("UserId", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //this dao method signout particular user in the database
    public UserEntity signOutUser(UserAuthEntity userAuthEntity) {
        LocalDateTime logoutTime = LocalDateTime.now();
        userAuthEntity.setLogoutTime(logoutTime);
        entityManager.persist(userAuthEntity);
        return userAuthEntity.getUser();
    }

    //this dao method delete a user in the database
    public UserEntity deleteUser(UserEntity userEntity) {
        entityManager.remove(userEntity);
        return userEntity;
    }
}