package com.github.costacarol.coopmeeting.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
public final class Associate {

    @Id
    @Column(length = 11, nullable = false, unique = true)
    private String id;

    @Column(length = 255, nullable = false)
    private String fullName;

    public Associate(@NonNull String fullName, @NonNull String cpfNumber){
        this.fullName = fullName;
        this.id = cpfNumber;
    }
}
