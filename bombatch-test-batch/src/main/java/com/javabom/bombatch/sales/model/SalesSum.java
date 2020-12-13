package com.javabom.bombatch.sales.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class SalesSum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate orderDate;

    @Column
    private long amountSum;

    @Builder
    public SalesSum(LocalDate orderDate, long amountSum) {
        this.orderDate = orderDate;
        this.amountSum = amountSum;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setAmountSum(long amountSum) {
        this.amountSum = amountSum;
    }
}