package com.backend.apiserver.repository;

import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, PagingAndSortingRepository<User, Long> {

	/**
	 * find an user with username and status
	 *
	 * @param username
	 * @param status
	 * @return User
	 */
	User findByUsernameAndStatus(String username, Status status);

	/**
	 * find an user with username or email and status
	 *
	 * @param username
	 * @param status
	 * @return User
	 */
	@Query(value = "select * from users where (username = ?1 or email = ?2) and status = ?3", nativeQuery = true)
	User findByUsernameOrEmailAndStatus(String username, String email, String status);

	/**
	 * Check if user with username exists
	 *
	 * @param username
	 * @return
	 */
	boolean existsByUsername(String username);

	/**
	 * Check if user with email exists
	 *
	 * @param email
	 * @return
	 */
	boolean existsByEmail(String email);

	/**
	 * find an user with id and status
	 *
	 * @param id
	 * @return User
	 */
	User findByIdAndStatus(Long id, Status status);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	User findUserByIdAndStatus(Long id, Status status);

	@Query(value = "select u.* from users u inner join user_details ud on u.id = ud.user_id " +
			"where u.email like %:keyword% or u.username like %:keyword% or ud.full_name like %:keyword%", nativeQuery = true)
	Page<User> findAllByEmailOrUsernameOrFullName(@Param("keyword") String keyword, Pageable pageable);

	Integer countUserByStatus(Status status);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	User findUserByUsername(String username);

	List<User> findAllByStatus(Status status);
}
