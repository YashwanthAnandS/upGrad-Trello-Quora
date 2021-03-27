package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AuthTokenDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthTokenDao authTokenDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    //This service class method fetch username and call dao method to fetch username from the database
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserByUsername(String username) {
        UserEntity userEntity = userDao.getUserByUsername(username);
        return userEntity;
    }

    //This service class method fetch emailId and call dao method to fetch emailId from the database
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserByEmail(String email) {
        UserEntity userEntity = userDao.getUserByEmail(email);
        return userEntity;
    }

    //This service class method sign up specific user
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signupUser(UserEntity user) throws SignUpRestrictedException {

        if (userDao.getUserByEmail(user.getEmail()) != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        if (userDao.getUserByUsername(user.getUserName()) != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        String[] array = passwordCryptographyProvider.encrypt(user.getPassword());
        user.setPassword(array[1]);
        user.setSalt(array[0]);
        userDao.signupUser(user);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUsername(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());  //encrypt password and salt
        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);// extracts the token from the JWT token string
            UserAuthEntity userAuthTokenEntity = new UserAuthEntity(); //set user Authentication details
            userAuthTokenEntity.setUser(userEntity);
            userAuthTokenEntity.setUuid(UUID.randomUUID().toString());
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUUID(), now, expiresAt));
            userAuthTokenEntity.setLoginTime(now); //set login time
            userAuthTokenEntity.setExpiryTime(expiresAt); // set expires time
            userDao.saveLoginInfo(userAuthTokenEntity);
            return userAuthTokenEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signOut(final String accessToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthTokenEntity = authTokenDao.checkAuthToken(accessToken); // fetch access token and check whether auth token is null or not
        if (userAuthTokenEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        } else {
            userAuthTokenEntity.setLogoutTime(ZonedDateTime.now());
            return userDao.updateUser(userAuthTokenEntity);
        }
    }


    //This service class method fetch specific user uuid
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserByUuid(String uuid) throws UserNotFoundException {
        UserEntity user = userDao.getUserByUuid(uuid);
        if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        return user;
    }
}