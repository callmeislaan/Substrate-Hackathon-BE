package com.backend.apiserver.repository;

import com.backend.apiserver.entity.MentorRejection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRejectionRepository extends JpaRepository<MentorRejection, Long> {
}
