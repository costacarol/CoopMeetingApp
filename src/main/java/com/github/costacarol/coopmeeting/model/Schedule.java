package com.github.costacarol.coopmeeting.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer scheduleId;
    private Date date;
    private Boolean sessionVoteIsOpen = false;
    private String voteResult;

    public Schedule(Date date){
        this.date = date;
    }

}
