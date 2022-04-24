package com.backend.apiserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "money_exchange_histories")
public class MoneyExchangeHistory extends BaseEntity {

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "mentor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Mentor mentor;

    private int amount;

    @Enumerated(EnumType.STRING)
    private Status status;
}
