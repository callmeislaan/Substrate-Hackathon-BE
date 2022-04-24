package com.backend.apiserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "achievements")
public class Achievement extends BaseEntity {

    private String title;

    @Column(columnDefinition="TEXT")
    private String content;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "mentor_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Mentor mentor;
}
