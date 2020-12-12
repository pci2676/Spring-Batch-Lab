package com.javabom.bombatch.sales.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private long amount;

    @Column
    private Long ownerNo;

    @Column
    private Long orderNo;

    @Column
    private LocalDate orderDate;

    @Builder
    public Sales(long amount, Long ownerNo, LocalDate orderDate, Long orderNo) {
        this.amount = amount;
        this.ownerNo = ownerNo;
        this.orderDate = orderDate;
        this.orderNo = orderNo;
    }
}