package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AuthTokenDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthTokenService {
    @Autowired
    AuthTokenDao authTokenDao;

    private Map<String, String> error;

    // This method checks existing auth token, and then signs out the user if the auth token is found. If the auth token is not found, it displays an error message.

    @Transactional
    public UserEntity checkAuthToken(String authToken) throws SignOutRestrictedException {

        UserAuthEntity userAuthEntity = authTokenDao.checkAuthToken(authToken);
        if (userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        } else {
            return authTokenDao.signOutUser(userAuthEntity);
        }
    }

    // This method checks the auth token to validate if the user is signed in. If auth token is found, it returns the user details matching the auth token.
    public UserEntity checkAuthentication(String authToken, String methodName) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = authTokenDao.checkAuthToken(authToken);
        LocalDateTime now = LocalDateTime.now();

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", error.get(methodName + ".ATHR-001"));
        } else if (userAuthEntity.getLogoutTime() != null) {
            throw new AuthorizationFailedException("ATHR-002", error.get(methodName + ".ATHR-002"));
        } else {
            return userAuthEntity.getUser();
        }
    }


}