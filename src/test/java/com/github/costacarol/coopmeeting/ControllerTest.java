package com.github.costacarol.coopmeeting;

import com.github.costacarol.coopmeeting.controller.Controller;
import com.github.costacarol.coopmeeting.model.ApiResponse;
import com.github.costacarol.coopmeeting.model.Associate;
import com.github.costacarol.coopmeeting.model.OptionOfVote;
import com.github.costacarol.coopmeeting.model.Schedule;
import com.github.costacarol.coopmeeting.repository.AssociateRepository;
import com.github.costacarol.coopmeeting.repository.OptionOfVoteRepository;
import com.github.costacarol.coopmeeting.repository.ScheduleRepository;
import com.github.costacarol.coopmeeting.repository.VoteRepository;
import com.github.costacarol.coopmeeting.service.Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerTest {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AssociateRepository associateRepository;
    @Autowired
    private OptionOfVoteRepository optionOfVoteRepository;
    @Autowired
    private VoteRepository voteRepository;

    @MockBean
    private Service service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    Controller controller;

    @Test
    void addNewSchedule() throws Exception {
        when(this.service.addNewSchedule()).thenReturn(new Schedule());
        this.mockMvc.perform(MockMvcRequestBuilders.post("/meeting/schedule"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(201));
    }

    @Test
    void addNewAssociate() throws Exception {
        when(this.service.addNewAssociate(anyString(), anyString())).thenReturn(new Associate("teste", "12345678910"));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/meeting/associate/teste/12345678910"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(201));
    }

    @Test
    void addNewVoteOption() throws Exception {
        when(this.service.addNewVoteOption(anyString())).thenReturn(new OptionOfVote("yes"));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/meeting/vote-option/yes"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(201));
    }

    @Test
    void shouldReturnSuccessWhenAddNewVote() throws Exception {
        when(this.service.addNewVote(any(), any(), anyString())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/meeting/vote/1/1/11111111111"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(201));
    }

    @Test
    void shouldReturnBadRequestWhenAddNewVote() throws Exception {
        when(this.service.addNewVote(any(), any(), anyString())).thenReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/meeting/vote/1/1/1111111"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void shouldReturnSuccessOnComputeVotesWithDraw() throws Exception {
        when(this.service.computeVotingResults(any())).thenReturn(new ApiResponse("Draw"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/meeting/compute/1"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void shouldReturnSuccessOnComputeVotesWithYesResult() throws Exception {
        when(this.service.computeVotingResults(any())).thenReturn(new ApiResponse("Yes"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/meeting/compute/1"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void shouldReturnSuccessOnComputeVotesWithNoResult() throws Exception {
        when(this.service.computeVotingResults(any())).thenReturn(new ApiResponse("No"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/meeting/compute/1"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void shouldReturnBadRequestOnComputeVotesWithNoSchedule() throws Exception {
        when(this.service.computeVotingResults(any())).thenReturn(new ApiResponse("It was not possible to compute the votes, check the schedule inserted or if the session was closed"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/meeting/compute/1"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void shouldReturnSuccessInOpenSessionDefault() throws Exception {
        when(this.service.openSessionVotes(any())).thenReturn(Optional.of(new Schedule(new Date(System.currentTimeMillis()))));
        when(this.service.closeSessionVotes(any(), any())).thenReturn(Optional.of(new Schedule(new Date(System.currentTimeMillis()))));
        this.mockMvc.perform(MockMvcRequestBuilders.put("/meeting/open-session/1"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(202));
    }

    @Test
    void shouldReturnBadRequestInOpenSessionDefault() throws Exception {
        when(this.service.openSessionVotes(any())).thenReturn(Optional.empty());
        this.mockMvc.perform(MockMvcRequestBuilders.put("/meeting/open-session/1"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void shouldReturnBadRequestInOpenSessionWithTime() throws Exception {
        when(this.service.openSessionVotes(any())).thenReturn(Optional.empty());
        this.mockMvc.perform(MockMvcRequestBuilders.put("/meeting/open-session/1/1"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void shouldReturnSuccessInOpenSessionWithTime() throws Exception {
        when(this.service.openSessionVotes(any())).thenReturn(Optional.of(new Schedule(new Date(System.currentTimeMillis()))));
        when(this.service.closeSessionVotes(any(), any())).thenReturn(Optional.of(new Schedule(new Date(System.currentTimeMillis()))));
        this.mockMvc.perform(MockMvcRequestBuilders.put("/meeting/open-session/1/1"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(202));
    }
}