package com.github.costacarol.coopmeeting;

import com.github.costacarol.coopmeeting.model.Associate;
import com.github.costacarol.coopmeeting.model.OptionOfVote;
import com.github.costacarol.coopmeeting.model.Schedule;
import com.github.costacarol.coopmeeting.repository.AssociateRepository;
import com.github.costacarol.coopmeeting.repository.OptionOfVoteRepository;
import com.github.costacarol.coopmeeting.repository.ScheduleRepository;
import com.github.costacarol.coopmeeting.repository.VoteRepository;
import com.github.costacarol.coopmeeting.service.Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.Date;
import java.util.Optional;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;


@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class ServiceTest {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AssociateRepository associateRepository;
    @Autowired
    private OptionOfVoteRepository optionOfVoteRepository;
    @Autowired
    private VoteRepository voteRepository;

    private Service service;

    @BeforeEach
    public void setUp(){
        this.service = new Service(associateRepository, scheduleRepository, optionOfVoteRepository, voteRepository);
    }

    @Test
    void shouldCreateANewSchedule() {
        Schedule schedule = this.service.addNewSchedule();
        Assertions.assertNotNull(schedule.getScheduleId());
    }

    @Test
    void shouldCreateANewVoteOption() {
        OptionOfVote optionOfVote = this.service.addNewVoteOption("yes");
        Assertions.assertNotNull(optionOfVote.getIdVote());
    }

    @Test
    void shouldCreateANewAssociate() {
        Associate associate = (Associate) this.service
                .addNewAssociate("Carolina Costa", "12345678914");
        Optional<Associate> associate2 = associateRepository.findById("12345678914");
        Assertions.assertNotNull(associate.getId());
        Assertions.assertEquals(associate2.get(), associate);
    }

    @Test
    void shouldNotCreateANewAssociateWithSameCPF() {
        Associate associate = (Associate) this.service
                .addNewAssociate("Carolina Costa", "12345678914");
        Assertions.assertEquals(this.service
                .addNewAssociate("Carolina Costa", "12345678914").toString(), "ApiResponse(descriptionResponse=Associate not found or invalid CPF number was inserted.)");
        Assertions.assertEquals(associateRepository.findById("12345678914").get(), associate);
    }

    @Test
    void shouldNotCreateANewAssociateWithInsufficientNumberInTheCPF() {
        Assertions.assertEquals(this.service
                .addNewAssociate("Carolina Costa", "1234567897").toString(), "ApiResponse(descriptionResponse=Associate not found or invalid CPF number was inserted.)");
    }

    @Test
    void shouldCreateANewVote() {
        this.service.addNewAssociate("Ana Silva", "11223344556");
        Schedule schedule = new Schedule(new Date(System.currentTimeMillis()));
        schedule.setSessionVoteIsOpen(true);
        this.scheduleRepository.save(schedule);
        Assertions.assertTrue(this.service.addNewVote(schedule.getScheduleId(), 3, "11223344556"));
        Assertions.assertNotNull(voteRepository.getVotesByScheduleId(schedule.getScheduleId()));
    }

    @Test
    void shouldNotCreateANewVoteWithSessionClosed() {
        this.service.addNewAssociate("Ana Silva", "11223344556");
        Schedule schedule = new Schedule(new Date(System.currentTimeMillis()));
        this.scheduleRepository.save(schedule);
        Assertions.assertFalse(this.service.addNewVote(schedule.getScheduleId(), 3, "11223344556"));
    }

    @Test
    void shouldNotCreateANewVoteWithNoValidSchedule() {
        this.service.addNewAssociate("Ana Silva", "11223344556");
        Assertions.assertFalse(this.service.addNewVote(500, 3, "11223344556"));
    }

    @Test
    void shouldNotCreateANewVoteWithNoValidAssociate() {
        Schedule schedule = new Schedule(new Date(System.currentTimeMillis()));
        schedule.setSessionVoteIsOpen(true);
        this.service.addNewAssociate("Ana Silva", "11223344556");
        this.scheduleRepository.save(schedule);
        Assertions.assertFalse(this.service.addNewVote(schedule.getScheduleId(), 3, "00000000000"));
    }

    @Test
    void shouldNotCreateANewVoteWithNoValidOptionOfVote() {
        Schedule schedule = new Schedule(new Date(System.currentTimeMillis()));
        schedule.setSessionVoteIsOpen(true);
        this.service.addNewAssociate("Ana Silva", "11223344556");
        this.scheduleRepository.save(schedule);
        Assertions.assertFalse(this.service.addNewVote(schedule.getScheduleId(), 0, "11223344556"));
    }

    @Test
    void shouldNotCreateANewVoteWithNullOptionOfVote() {
        Schedule schedule = new Schedule(new Date(System.currentTimeMillis()));
        schedule.setSessionVoteIsOpen(true);
        this.scheduleRepository.save(schedule);
        this.service.addNewAssociate("Ana Silva", "11223344556");
        Exception exception = Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> this.service.addNewVote(schedule.getScheduleId(), null, "11223344556"));
    }

    @Test
    void shouldComputeVotingResultsAndReturnYesWinner() {
        Schedule schedule = this.service.addNewSchedule();
        schedule.setSessionVoteIsOpen(true);
        scheduleRepository.save(schedule);

        this.service.addNewAssociate("Ana Silva", "11223344556");
        this.service.addNewAssociate("Luan Silva", "22334455667");
        this.service.addNewAssociate("Marcos Silva", "33445566778");

        OptionOfVote optionOfVoteYes = this.service.addNewVoteOption("yes");
        OptionOfVote optionOfVoteNo = this.service.addNewVoteOption("no");

        this.service.addNewVote(schedule.getScheduleId(), optionOfVoteNo.getIdVote(), "11223344556");
        this.service.addNewVote(schedule.getScheduleId(), optionOfVoteYes.getIdVote(), "22334455667");
        this.service.addNewVote(schedule.getScheduleId(), optionOfVoteYes.getIdVote(), "33445566778");

        schedule.setSessionVoteIsOpen(false);
        scheduleRepository.save(schedule);
        Assertions.assertEquals("yes", this.service.computeVotingResults(schedule.getScheduleId()).getDescriptionResponse());
    }

    @Test
    void shouldComputeVotingResultsAndReturnNoWinner() {
        Schedule schedule = this.service.addNewSchedule();
        schedule.setSessionVoteIsOpen(true);
        scheduleRepository.save(schedule);

        this.service.addNewAssociate("Ana Silva", "11223344556");
        this.service.addNewAssociate("Luan Silva", "22334455667");
        this.service.addNewAssociate("Marcos Silva", "33445566778");

        OptionOfVote optionOfVoteYes = this.service.addNewVoteOption("yes");
        OptionOfVote optionOfVoteNo = this.service.addNewVoteOption("no");

        this.service.addNewVote(schedule.getScheduleId(), optionOfVoteYes.getIdVote(), "11223344556");
        this.service.addNewVote(schedule.getScheduleId(), optionOfVoteNo.getIdVote(), "22334455667");
        this.service.addNewVote(schedule.getScheduleId(), optionOfVoteNo.getIdVote(), "33445566778");

        schedule.setSessionVoteIsOpen(false);
        scheduleRepository.save(schedule);
        Assertions.assertEquals("no", this.service.computeVotingResults(schedule.getScheduleId()).getDescriptionResponse());
    }

    @Test
    void shouldComputeVotingResultsAndReturnDraw() {
        Schedule schedule = this.service.addNewSchedule();
        schedule.setSessionVoteIsOpen(true);
        scheduleRepository.save(schedule);

        this.service.addNewAssociate("Ana Silva", "11223344556");
        this.service.addNewAssociate("Luan Silva", "22334455667");
        this.service.addNewAssociate("Marcos Silva", "33445566778");

        OptionOfVote optionOfVoteYes = this.service.addNewVoteOption("yes");
        OptionOfVote optionOfVoteNo = this.service.addNewVoteOption("no");

        this.service.addNewVote(schedule.getScheduleId(), optionOfVoteNo.getIdVote(), "11223344556");
        this.service.addNewVote(schedule.getScheduleId(), optionOfVoteYes.getIdVote(), "33445566778");

        schedule.setSessionVoteIsOpen(false);
        scheduleRepository.save(schedule);
        Assertions.assertEquals("Draw", this.service.computeVotingResults(schedule.getScheduleId()).getDescriptionResponse());
    }

    @Test
    void shouldComputeVotingResultsWithNoVotes() {
        Schedule schedule = this.service.addNewSchedule();

        this.service.addNewAssociate("Ana Silva", "11223344556");
        this.service.addNewAssociate("Luan Silva", "22334455667");
        this.service.addNewAssociate("Marcos Silva", "33445566778");

        Assertions.assertEquals("There are no votes to compute.", this.service.computeVotingResults(schedule.getScheduleId()).getDescriptionResponse());
    }

    @Test
    public void shouldOpenSessionToVote(){
        Schedule schedule = this.service.addNewSchedule();
        this.service.openSessionVotes(schedule.getScheduleId());
        Assertions.assertTrue(schedule.getSessionVoteIsOpen());
    }

    @Test
    public void shouldReturnAnEmptyOptionalOnOpenSessionToVote() {
        Schedule schedule = this.service.addNewSchedule();
        schedule.setSessionVoteIsOpen(true);
        Assertions.assertEquals(Optional.empty(), this.service.openSessionVotes(schedule.getScheduleId()));
    }

    @Test
    public void shouldReturnAnEmptyOptionalOnCloseSessionToVote() {
        Schedule schedule = this.service.addNewSchedule();
        schedule.setSessionVoteIsOpen(false);
        Assertions.assertEquals(Optional.empty(), this.service.closeSessionVotes(schedule.getScheduleId(), 1L));
    }

    @Test
    public void shouldCloseSessionToVote() {
        Schedule schedule = this.service.addNewSchedule();
        schedule.setSessionVoteIsOpen(true);
        this.service.closeSessionVotes(schedule.getScheduleId(), 1L);
        Assertions.assertFalse(schedule.getSessionVoteIsOpen());
    }
}