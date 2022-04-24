package com.backend.apiserver.repository;

import com.backend.apiserver.entity.RequestAnnouncement;
import com.backend.apiserver.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;

public interface RequestAnnouncementRepository extends JpaRepository<RequestAnnouncement, Long> {
	void deleteAllByRequestId(Long requestId);
	void deleteByRequestIdAndMentorId(Long requestId, Long mentorId);
	void deleteAllByRequestIdAndStatus(Long requestId, Status status);
	RequestAnnouncement findByIdAndStatus(Long id, Status status);
	List<RequestAnnouncement> findAllByMentorIdAndStatus(Long id, Status status);
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	RequestAnnouncement findByRequestIdAndMentorIdAndStatus(Long requestId, Long mentorId, Status status);
	List<RequestAnnouncement> findByRequestId(Long requestId);
	boolean existsByRequestIdAndStatus(Long requestId, Status status);
	RequestAnnouncement findByRequestIdAndStatus(Long requestId, Status status);
}
