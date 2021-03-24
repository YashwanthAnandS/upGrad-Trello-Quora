package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    //This method creates a new answer for the entered question id
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") final String questionId, final AnswerRequest answerRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());

        final AnswerEntity createdAnswerEntity = answerBusinessService.createAnswer(answerEntity, authorization, questionId);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    //PUT Method for Editing Answer Content using AnswerId
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest, @PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        final AnswerEntity answerEntity = answerBusinessService.editAnswer(answerEditRequest.getContent(), answerId, authorization);

        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerEntity.getUuid()).status("ANSWER EDITED");

        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    //DELETE Method for Deleting Answer using Answer Id
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        final AnswerEntity answerEntity = answerBusinessService.deleteAnswer(answerId, authorization);

        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");

        return  new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }

    //GET method to get all Answers Corresponding to a question
    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        final List<AnswerEntity> answerEntityList = answerBusinessService.getAllAnswersByQuestion(questionId, authorization);

        List<AnswerDetailsResponse> answerDetailsResponseList = new ArrayList<>();

        for (int i = 0; i < answerEntityList.size(); i++) {
            answerDetailsResponseList.add(new AnswerDetailsResponse()
                    .id(answerEntityList.get(i).getUuid())
                    .answerContent(answerEntityList.get(i).getAns()));
        }
        return new ResponseEntity<>(answerDetailsResponseList, HttpStatus.OK);
    }


}
