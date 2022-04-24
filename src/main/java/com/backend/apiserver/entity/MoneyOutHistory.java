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

@Data
@Entity
@Table(name = "money_out_histories")
public class MoneyOutHistory extends BaseEntity {
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "mentor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Mentor mentor;

    @Column(name = "payment_method_id")
    private Long paymentMethodId;

    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method_name")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private Status status;
}
