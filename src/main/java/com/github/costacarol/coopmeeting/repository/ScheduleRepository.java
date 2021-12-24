package com.github.costacarol.coopmeeting.repository;

import com.github.costacarol.coopmeeting.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends CrudRepository<Schedule, Integer> {
}
