package com.github.costacarol.coopmeeting.controller;

import com.github.costacarol.coopmeeting.model.ApiResponse;
import com.github.costacarol.coopmeeting.service.Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/meeting")
@Api(value = "Meeting API Rest")
@CrossOrigin(origins = "*")
public class Controller {

    private Service service;

    @PostMapping(path="/schedule")
    @ApiOperation(value="Add a new Schedule")
    public ResponseEntity<Object> addNewSchedule(){
        return new ResponseEntity<>(service.addNewSchedule(), HttpStatus.CREATED);
    }

    @PostMapping(path="/associate/{fullName}/{cpfNumber}")
    @ApiOperation(value="Add a new Associate")
    public ResponseEntity<Object> addNewAssociate(@PathVariable("fullName") String fullName,
                                                  @PathVariable("cpfNumber") String cpfNumber) {
        Object response = service.addNewAssociate(fullName, cpfNumber);
        if (response.getClass().getName().substring(40).equals("Associate")) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path="/vote-option/{description}")
    @ApiOperation(value="Add a new vote option")
    public ResponseEntity<Object> addNewVoteOption(@PathVariable("description") String description){
        return new ResponseEntity<>(service.addNewVoteOption(description), HttpStatus.CREATED);
    }

    @PostMapping(path="/vote/{scheduleId}/{voteId}/{associateId}")
    @ApiOperation(value="Add a new vote")
    public ResponseEntity<Object> addNewVote(@PathVariable("scheduleId") Integer scheduleId,
                                             @PathVariable("voteId") Integer voteId,
                                             @PathVariable("associateId") String associateId){

        Boolean response = service.addNewVote(scheduleId, voteId, associateId);
        if(response) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path="/compute/{scheduleId}")
    @ApiOperation(value="Compute the votes")
    public ResponseEntity<Object> computeVotes(@PathVariable("scheduleId") Integer scheduleId) {
        ApiResponse compute = service.computeVotingResults(scheduleId);
        String error = "It was not possible to compute the votes, check the schedule inserted or if the session was closed";
        if (!Objects.equals(compute.getDescriptionResponse(), error)) {
            return new ResponseEntity<>(compute, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(compute, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path="/open-session/{scheduleId}")
    @ApiOperation(value="Open session and close in 60 seconds")
    public ResponseEntity<Object> openSessionDefault(@PathVariable("scheduleId") Integer scheduleId) {
        Optional optional = service.openSessionVotes(scheduleId);
        if (optional.isPresent()) {
            service.closeSessionVotes(scheduleId, 60L);
            return new ResponseEntity<>("The session was open and will be close in 60 seconds.", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("The schedule does not exist or the session is already open.", HttpStatus.BAD_REQUEST);
    }

    @PutMapping(path="/open-session/{scheduleId}/{time}")
    @ApiOperation(value="Open session and close in {time} seconds")
    public ResponseEntity<Object> openSessionWithTime(@PathVariable("scheduleId") Integer scheduleId,
                                                      @PathVariable("time") Long time) {
        Optional optional = service.openSessionVotes(scheduleId);
        if (optional.isPresent()) {
            service.closeSessionVotes(scheduleId, time);
            return new ResponseEntity<>("The session was open and will be close in " + time + " seconds.", HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("The schedule does not exist or the session is already open.", HttpStatus.BAD_REQUEST);
        }
    }
}
