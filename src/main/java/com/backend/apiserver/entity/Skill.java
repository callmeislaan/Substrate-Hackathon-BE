package com.backend.apiserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "skills",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name"),
        })
public class Skill extends BaseEntity {

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany(mappedBy = "skills")
    private Set<Request> requests = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "skill",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<MentorSkill> mentorSkills = new HashSet<>();
}
