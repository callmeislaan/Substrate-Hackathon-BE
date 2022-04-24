package com.backend.apiserver.repository;

import com.backend.apiserver.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;

public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {

	/**
	 * Find user by user id
	 *
	 * @param userId user id
	 * @return UserDetail
	 */
	UserDetail findByUserId(Long userId);

	/**
	 * Find and lock user detail table for update
	 *
	 * @param userId
	 * @return UserDetail
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	UserDetail findUserDetailByUserId(Long userId);

	@Query(value = "select ud.* from user_details ud inner join users u on ud.user_id = u.id where u.username = :username and u.status = :status for update", nativeQuery = true)
	UserDetail findUserDetailByUsernameAndStatus(String username, String status);
}
