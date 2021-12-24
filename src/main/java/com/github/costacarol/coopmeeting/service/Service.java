package com.github.costacarol.coopmeeting.service;

import com.github.costacarol.coopmeeting.model.*;
import com.github.costacarol.coopmeeting.repository.AssociateRepository;
import com.github.costacarol.coopmeeting.repository.OptionOfVoteRepository;
import com.github.costacarol.coopmeeting.repository.ScheduleRepository;
import com.github.costacarol.coopmeeting.repository.VoteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@AllArgsConstructor
@Log4j2
public class Service{

    private AssociateRepository associateRepository;
    private ScheduleRepository scheduleRepository;
    private OptionOfVoteRepository optionOfVoteRepository;
    private VoteRepository voteRepository;

    public Schedule addNewSchedule(){
        Schedule schedule = new Schedule(new Date(System.currentTimeMillis()));
        scheduleRepository.save(schedule);
        return schedule;
    }

    public OptionOfVote addNewVoteOption(String description){
        OptionOfVote optionsOfVotes = new OptionOfVote(description);
        optionOfVoteRepository.save(optionsOfVotes);
        return optionsOfVotes;
    }

    public Object addNewAssociate(String fullName, String cpfNumber) {
        Optional<Associate> associate = associateRepository.findById(cpfNumber);
        if (associate.isEmpty() && cpfNumber.length() == 11) {
            Associate newAssociate = new Associate(fullName, cpfNumber);
            associateRepository.save(newAssociate);
            return newAssociate;
        }else {
            return new ApiResponse("Associate not found or invalid CPF number was inserted.");
        }
    }

    private Boolean validateDataToVote(Integer scheduleId, Integer voteId, String associateId){
        return optionOfVoteRepository.existsById(voteId)
                && associateRepository.existsById(associateId)
                && scheduleRepository.existsById(scheduleId);
    }

    public Boolean addNewVote(Integer scheduleId, Integer voteId, String associateId) {
        if (this.validateDataToVote(scheduleId, voteId, associateId)) {
            Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
            if ((voteRepository.getVotesByScheduleId(scheduleId).stream()
                    .noneMatch(v -> v.getIdAssociate().equals(associateId))
                    && (schedule.get().getSessionVoteIsOpen()))) {
                Vote vote = new Vote(voteId, associateId, scheduleId);
                voteRepository.save(vote);
                return true;
            }
        }
        return false;
    }

    private Integer countVotes(Integer scheduleId){
        List<Vote> votes = voteRepository.getVotesByScheduleId(scheduleId);
        if(!votes.isEmpty()) {
            List<Integer> id = votes.stream().map(Vote::getIdVoteOption).collect(Collectors.toList());
            Set<Integer> uniqueSet = new HashSet<>(id);
            Integer winner = 0;
            Integer times = 0;
            for (Integer i : uniqueSet) {
                if ((Collections.frequency(id, i)) > times) {
                    winner = i;
                    times = Collections.frequency(id, i);
                }else if((Collections.frequency(id, i)) == times){
                    winner = 0;
                }
            }
            return winner;
        }
        return -1;
    }

    public ApiResponse computeVotingResults(Integer scheduleId) {
        Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
        if (schedule.isPresent() && !schedule.get().getSessionVoteIsOpen()) {
            Integer winner = this.countVotes(scheduleId);
            if (winner >= 1) {
                Optional<OptionOfVote> win = optionOfVoteRepository.findById(winner);
                schedule.get().setVoteResult(win.get().getDescriptionVote());
                scheduleRepository.save(schedule.get());
                return new ApiResponse(win.get().getDescriptionVote());
            }else if(winner == 0) {
                return new ApiResponse("Draw");
            }else{
                return new ApiResponse("There are no votes to compute.");
            }
        }
        return new ApiResponse("It was not possible to compute the votes, check the schedule inserted or if the session was closed");
    }

    @Async
    public Optional closeSessionVotes(Integer scheduleId, Long time) {
        Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
        if (schedule.isPresent() && schedule.get().getSessionVoteIsOpen()) {
            try {
                TimeUnit.SECONDS.sleep(time);
                schedule.get().setSessionVoteIsOpen(false);
                log.info("Session is closed.");
                scheduleRepository.save(schedule.get());
                return schedule;
            } catch (InterruptedException e) {
                log.error("Error: " + e.getMessage());
            }
        }
        return Optional.empty();
    }

        public Optional openSessionVotes(Integer scheduleId){
            Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
            if (schedule.isPresent() && !schedule.get().getSessionVoteIsOpen()) {
                schedule.get().setSessionVoteIsOpen(true);
                log.info("Session is open.");
                scheduleRepository.save(schedule.get());
                return schedule;
            }
            return Optional.empty();
        }
}