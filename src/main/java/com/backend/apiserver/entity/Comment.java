package com.backend.apiserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "mentor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Mentor mentor;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(columnDefinition="TEXT")
    private String content;

    private int rating;
}
