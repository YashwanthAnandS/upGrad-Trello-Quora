package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AuthTokenDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonControllerService {

    @Autowired
    AuthTokenDao authTokenDao;

    //This service class method fetch user details if user is authenticated
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserDetails(final String uuid, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuthTokenEntity = authTokenDao.checkAuthToken(accessToken); // fetch accesstoken and check whether auth token is null or not
        UserEntity user = authTokenDao.getUserByUuid(uuid);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");

        }

        if (userAuthTokenEntity.getLogoutTime() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        return user;
    }
}
