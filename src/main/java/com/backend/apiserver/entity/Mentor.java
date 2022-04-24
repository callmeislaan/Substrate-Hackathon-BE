
package com.backend.apiserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "mentors")
public class Mentor extends BaseEntity {

    @MapsId
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "anest_mentor")
    private boolean isAnestMentor;

    private int price;

    private String job;

    @Column(columnDefinition="TEXT")
    private String introduction;

    @Column(name = "skill_description", columnDefinition = "TEXT")
    private String skillDescription;

    @Column(name = "total_bank_account")
    private int totalBankAccount;

    @Column(name = "total_ewallet")
    private int totalEWallet;

    @Column(name = "total_money_in")
    private int totalMoneyIn;

    @Column(name = "total_money_out")
    private int totalMoneyOut;

    @Column(name = "total_money_current")
    private int totalMoneyCurrent;

    @Column(name = "total_request_receive")
    private int totalRequestReceive;

    @Column(name = "total_request_finish")
    private int totalRequestFinish;

    @Column(name = "total_request_deny")
    private int totalRequestDeny;

    @Column(name = "total_hours_be_hired")
    private int totalHoursBeHired;

    @Column(columnDefinition="TEXT")
    private String service;

    @Column(name = "total_rating_1")
    private int totalRating1;

    @Column(name = "total_rating_2")
    private int totalRating2;

    @Column(name = "total_rating_3")
    private int totalRating3;

    @Column(name = "total_rating_4")
    private int totalRating4;

    @Column(name = "total_rating_5")
    private int totalRating5;

    @Column(name = "average_rating")
    private float averageRating;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<MoneyExchangeHistory> moneyExchangeHistories = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<MoneyOutHistory> moneyOutHistories = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "mentor",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Fetch(FetchMode.SUBSELECT)
    private Set<Achievement> achievements = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY)
    private Set<BankCard> bankCards = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY)
    private Set<EWallet> eWallets = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<MentorRequest> mentorRequests = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<RequestAnnouncement> requestAnnouncements = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<RequestFollowing> requestFollowings = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "mentor",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Fetch(FetchMode.SUBSELECT)
    private List<MentorSkill> mentorSkills = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<Comment> comments = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Status status;
}
