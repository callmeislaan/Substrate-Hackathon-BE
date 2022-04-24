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
@Table(name = "e_wallets",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"mentor_id", "phone", "e_wallet_name"}),
        })
public class EWallet extends BaseEntity {

    @Column(name = "holder_name")
    private String holderName;

    @Column(name = "e_wallet_name")
    private String eWalletName;

    private String phone;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "mentor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Mentor mentor;

    @Enumerated(EnumType.STRING)
    private Status status;
}
