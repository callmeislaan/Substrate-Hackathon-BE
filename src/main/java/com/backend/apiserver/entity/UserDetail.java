package com.backend.apiserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_details")
public class UserDetail extends BaseEntity {

    @MapsId
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "full_name")
    private String fullName;

    private LocalDateTime dateOfBirth;

    private boolean gender;

    private String phone;

    @Column(columnDefinition="TEXT")
    private String avatar;

    @Column(name = "total_budget_in")
    private int totalBudgetIn;

    @Column(name = "total_budget_current")
    private int totalBudgetCurrent;

    @Column(name = "total_request_create")
    private int totalRequestCreate;

    @Column(name = "total_hours_hired_mentor")
    private int totalHoursHiredMentor;

    @Column(name = "total_people_hired")
    private int totalPeopleHired;
}
