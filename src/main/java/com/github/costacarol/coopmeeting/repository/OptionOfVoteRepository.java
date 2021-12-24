package com.github.costacarol.coopmeeting.repository;

import com.github.costacarol.coopmeeting.model.OptionOfVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionOfVoteRepository extends CrudRepository<OptionOfVote, Integer> {
}
