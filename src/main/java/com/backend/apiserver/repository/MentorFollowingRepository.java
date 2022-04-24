package com.backend.apiserver.repository;

import com.backend.apiserver.entity.MentorFollowing;
import com.backend.apiserver.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Set;

public interface MentorFollowingRepository extends JpaRepository<MentorFollowing, Long> {
	/**
	 * Find a following mentor by mentorId and requestId
	 *
	 * @param mentorId
	 * @param userId
	 * @return MentorFollowing
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	MentorFollowing findByMentorIdAndUserId(Long mentorId, Long userId);

	/**
	 * Unfollow all mentor
	 *
	 * @param deleteStatus
	 * @param activeStatus
	 * @param userId
	 * @return
	 */
	@Modifying
	@Query("update MentorFollowing mf set mf.status = ?1 where mf.status = ?2 and mf.user.id = ?3")
	void unfollowAllMentor(Status deleteStatus, Status activeStatus, Long userId);

	/**
	 * Find all following mentor by userId and status
	 *
	 * @param userId
	 * @param status
	 * @return List<MentorFollowing>
	 */
	Page<MentorFollowing> findAllByUserIdAndStatusOrderByLastModifiedDateDesc(Long userId, Status status, Pageable pageable);

	/**
	 * Get List Mentor Id By User Id
	 *
	 * @param userId
	 * @return List<Long>
	 */
	@Query(value = "select mf.mentor.id from MentorFollowing mf where mf.user.id = ?1")
	Set<Long> getListMentorIdByUserId(Long userId);

	boolean existsByMentorIdAndUserIdAndStatus(Long mentorId, Long userId, Status status);
}
