package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.AuthTokenService;
import com.upgrad.quora.service.business.CommonControllerService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {
    @Autowired
    CommonControllerService service;

    @Autowired
    AuthTokenService authTokenService;


    // this method fetches user details for the given user UUID
    @RequestMapping(path = "userprofile/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> fetchUserDetails(@PathVariable("userId") String uuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity user = service.getUserDetails(uuid, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(user.getFirstName()).lastName(user.getLastName())
                .userName(user.getUserName()).emailAddress(user.getEmail()).contactNumber(user.getContactNumber())
                .country(user.getCountry()).aboutMe(user.getAboutMe()).dob(user.getDob());
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);

    }
}