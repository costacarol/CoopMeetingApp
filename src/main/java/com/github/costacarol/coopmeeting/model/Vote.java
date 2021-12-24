package com.github.costacarol.coopmeeting.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer idVote;
    @NotNull(message = "Id of vote option is mandatory")
    private Integer idVoteOption;
    @NotNull(message = "Id of associate is mandatory")
    private String idAssociate;
    @NotNull(message = "Id of Schedule is mandatory")
    private Integer idSchedule;

    public Vote(Integer idVoteOption, String associateId, Integer scheduleId) {
        this.idVoteOption = idVoteOption;
        this.idAssociate = associateId;
        this.idSchedule = scheduleId;
    }
}
