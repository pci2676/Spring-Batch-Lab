package com.javabom.bombatch.sales.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private long txAmount;

    @Column
    private Long ownerNo;

    public Sales(long txAmount, Long ownerNo) {
        this.txAmount = txAmount;
        this.ownerNo = ownerNo;
    }
}