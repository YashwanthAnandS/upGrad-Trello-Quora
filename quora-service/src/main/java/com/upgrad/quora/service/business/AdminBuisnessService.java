package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AdminDao;
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
public class AdminBuisnessService {

    @Autowired
    AuthTokenDao authTokenDao;

    @Autowired
    AdminDao adminDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String uuid, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuthTokenEntity = authTokenDao.checkAuthToken(accessToken);

        UserEntity user = authTokenDao.getUserByUuid(uuid);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutTime() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        } else if (userAuthTokenEntity.getUser().getRole().equals("nonadmin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        } else if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        return authTokenDao.deleteUser(user);
    }
}