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
public class Tax {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private long amount;

    @Column
    private Long ownerNo;

    public Tax(long amount, Long ownerNo) {
        this.amount = amount;
        this.ownerNo = ownerNo;
    }
}
