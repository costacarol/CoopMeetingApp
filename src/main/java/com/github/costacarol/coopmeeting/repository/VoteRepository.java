package com.github.costacarol.coopmeeting.repository;

import com.github.costacarol.coopmeeting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository  extends CrudRepository<Vote, Integer> {

    @Query(value = "SELECT * FROM vote WHERE id_schedule = ?1", nativeQuery = true)
    public List<Vote> getVotesByScheduleId(Integer scheduleId);
}
