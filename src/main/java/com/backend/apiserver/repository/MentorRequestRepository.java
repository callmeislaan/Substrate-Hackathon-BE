package com.backend.apiserver.repository;

import com.backend.apiserver.entity.MentorRequest;
import com.backend.apiserver.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;

public interface MentorRequestRepository extends JpaRepository<MentorRequest, Long> {

	/**
	 * Find all mentor request by id, mentor id and pending status
	 *
	 * @param requestId
	 * @param mentorId
	 * @param status
	 * @return
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	MentorRequest findByRequestIdAndMentorIdAndStatus(Long requestId, Long mentorId, Status status);

	boolean existsByRequestIdAndMentorIdAndStatus(Long requestId, Long mentorId, Status status);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	MentorRequest findByRequestIdAndMentorId(Long requestId, Long mentorId);

	/**
	 * Find mentor request by id and status
	 *
	 * @param requestId
	 * @param status
	 * @return
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	MentorRequest findMentorRequestByRequestIdAndStatus(Long requestId, Status status);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<MentorRequest> findAllByMentorIdAndStatus(Long mentorId, Status status);

	List<MentorRequest> findAllByMentorIdAndStatusIn(Long mentorId, List<Status> statuses);

	List<MentorRequest> findAllByRequestIdAndStatus(Long requestId, Status status);

	void deleteAllByRequestId(Long requestId);

	void deleteByRequestIdAndMentorId(Long requestId, Long mentorId);

	void deleteAllByRequestIdAndStatus(Long requestId, Status status);

	boolean existsByMentorIdAndStatus(Long id, Status status);

	@Query(value = "select count(distinct mentor_id) from mentor_requests r where r.created_date >= ?1 and r.created_date <= ?2 and status != ?3", nativeQuery = true)
	Integer countMentorRequestByStartDateAndEndDate(LocalDateTime startDateTime, LocalDateTime endDateTime, String status);
}
