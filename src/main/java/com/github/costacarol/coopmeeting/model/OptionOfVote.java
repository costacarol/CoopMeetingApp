package com.github.costacarol.coopmeeting.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
public class OptionOfVote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer idVote;
    @Column(length = 255, nullable = false)
    private String descriptionVote;

    public OptionOfVote(@NotNull String descriptionVote){
        this.descriptionVote = descriptionVote;
    }
}
