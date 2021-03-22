package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AuthTokenDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthTokenService {
    @Autowired
    AuthTokenDao authTokenDao;


    // This method checks existing auth token, and then signs out the user if the auth token is found. If the auth token is not found, it displays an error message.
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity checkAuthToken(String authToken) throws SignOutRestrictedException {

        UserAuthEntity userAuthEntity = authTokenDao.checkAuthToken(authToken);
        if (userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        } else {
            return authTokenDao.signOutUser(userAuthEntity);
        }
    }


}