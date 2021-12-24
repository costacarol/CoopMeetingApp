package com.github.costacarol.coopmeeting.repository;

import com.github.costacarol.coopmeeting.model.Associate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssociateRepository extends CrudRepository<Associate, String> {
}
