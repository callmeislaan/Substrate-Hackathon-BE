package com.backend.apiserver.repository;

import com.backend.apiserver.entity.MentorHire;
import com.backend.apiserver.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

public interface MentorHireRepository extends JpaRepository<MentorHire, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    MentorHire findByIdAndUserIdAndStatus(Long id, Long userId, Status status);

}
