package com.backend.apiserver.repository;

import com.backend.apiserver.entity.RequestFollowing;
import com.backend.apiserver.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;

public interface RequestFollowingRepository extends JpaRepository<RequestFollowing, Long> {
	/**
	 * Find a following request by mentorId and requestId
	 *
	 * @param mentorId
	 * @param requestId
	 * @return RequestFollowing
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	RequestFollowing findByMentorIdAndRequestId(Long mentorId, Long requestId);

	boolean existsByMentorIdAndRequestIdAndStatus(Long mentorId, Long requestId, Status status);

	/**
	 * Unfollow all request
	 *
	 * @param deleteStatus
	 * @param activeStatus
	 * @param mentorId
	 * @return
	 */
	@Modifying
	@Query("update RequestFollowing rf set rf.status = ?1 where rf.status = ?2 and rf.mentor.id = ?3")
	void unfollowAllRequest(Status deleteStatus, Status activeStatus, Long mentorId);

	/**
	 * Find all following request by mentorId and status
	 *
	 * @param mentorId
	 * @param status
	 * @return List<RequestFollowing>
	 */
	Page<RequestFollowing> findAllByMentorIdAndStatusOrderByLastModifiedDateDesc(Long mentorId, Status status, Pageable pageable);
}
