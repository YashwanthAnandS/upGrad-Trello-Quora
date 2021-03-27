package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    // register a new user
    @PostMapping(path = "user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signUp(SignupUserRequest request) throws SignUpRestrictedException {

        // create and populate new user entity:
        UserEntity newUser = new UserEntity();
        newUser.setUUID(UUID.randomUUID().toString());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setUserName(request.getUserName());
        newUser.setEmail(request.getEmailAddress());
        newUser.setCountry(request.getCountry());
        newUser.setAboutMe(request.getAboutMe());
        newUser.setDob(request.getDob());
        newUser.setRole("nonadmin"); // default user role
        newUser.setContactNumber(request.getContactNumber());
        newUser.setPassword(request.getPassword());

        UserEntity registeredUser = userService.signupUser(newUser);

        SignupUserResponse response = new SignupUserResponse().
                id(registeredUser.getUUID()).
                status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupUserResponse>(response, HttpStatus.CREATED);

    }


    // sign in user and return JWT token
    @PostMapping(path = "user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signIn(@RequestHeader(name = "authorization") final String authorization)
            throws AuthenticationFailedException {

        byte[] authBytes = Base64.getDecoder().decode(authorization.split("Basic ")[1]); // extract base64 encoded username and password

        String authText = new String(authBytes);
        String[] auth = authText.split(":");  // split username and password

        final UserAuthEntity token = userService.authenticate(auth[0], auth[1]);  // decode username and password
        UserEntity user = token.getUser();
        SigninResponse response = new SigninResponse().id(user.getUUID()).
                message("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();

        headers.add("access-token", token.getAccessToken()); // add JWT token in header

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    //this method signout specific user
    @RequestMapping(method = RequestMethod.POST, path = "user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signOut(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {

        UserAuthEntity userAuthTokenEntity = userService.signOut(authorization);

        SignoutResponse signoutResponse = new SignoutResponse().id(userAuthTokenEntity.getUser().getUUID()).message("SIGNED OUT SUCCESSFULLY");

        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }


}
