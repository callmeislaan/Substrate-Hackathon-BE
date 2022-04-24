package com.backend.apiserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@Entity
@Table(name = "bank_cards",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"mentor_id", "account_number"}),
        })
public class BankCard extends BaseEntity {

    @Column(name = "holder_name")
    private String holderName;

    @Column(name = "account_number")
    private String accountNumber;

    private String bank;
    private String branch;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "mentor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Mentor mentor;

    @Enumerated(EnumType.STRING)
    private Status status;
}
