package com.backend.apiserver.repository;

import com.backend.apiserver.entity.Request;
import com.backend.apiserver.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long>, PagingAndSortingRepository<Request, Long>, JpaSpecificationExecutor<Request> {

	/**
	 * Find a request by id
	 *
	 * @param id id of request
	 * @return Request
	 */
	Request findRequestById(Long id);

	/**
	 * Find a request by id and status
	 *
	 * @param id     id of request
	 * @param userId user id
	 * @param status status active
	 * @return Request
	 */
	Request findByIdAndUserIdAndStatus(Long id, Long userId, Status status);

	/**
	 * Find a request by id and not is status
	 *
	 * @param id     id of request
	 * @param status status delete
	 * @return Request
	 */
	Request findByIdAndStatusNot(Long id, Status status);

	/**
	 * Find a request by id and status
	 *
	 * @param id     id of request
	 * @param status status delete
	 * @return Request
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Request findByIdAndStatus(Long id, Status status);

	/**
	 * Find request by id and status
	 *
	 * @param id
	 * @param status
	 * @return
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Request findRequestByIdAndUserIdAndStatus(Long id, Long userId, Status status);

	/**
	 * Check if user has request with id
	 *
	 * @param id
	 * @param userId
	 * @param status
	 * @return
	 */
	boolean existsByIdAndUserIdAndStatus(Long id, Long userId, Status status);

	/**
	 * Find all request created by user
	 *
	 * @param userId user id of request creator
	 * @return List<Request>
	 */
	Page<Request> findAllByUserId(Long userId, Pageable pageable);

	/**
	 * Find all request by status and order by id desc
	 *
	 * @param status only open status
	 * @return List<Request>
	 */
	List<Request> findFirst50ByStatusOrderByIdDesc(Status status);

	/**
	 * Find all request by status and order by id desc
	 *
	 * @param status only open status
	 * @return List<Request>
	 */
	@Query(value = "select * from requests r " +
			"where r.id <> ?1 and r.status = ?2 order by random() limit 10", nativeQuery = true)
	List<Request> findOtherRequests(Long requestId, String status);

	/**
	 * Get List Request Id By List Skill Id
	 *
	 * @param skillIds
	 * @return List<Long>
	 */
	@Query(value = "select distinct request_id from request_skills where skill_id in ?1", nativeQuery = true)
	List<Long> getListRequestIdByListSkillId(List<Long> skillIds);

	Page<Request> findAllByIdInAndStatus(List<Long> ids, Status status, Pageable pageable);

	Page<Request> findAllByIdIn(List<Long> ids, Pageable pageable);

	@Query(value = "SELECT r.* FROM requests r inner join request_announcements ra on r.id = ra.request_id where ra.status = :status", nativeQuery = true)
	Page<Request> findAllConflictRequest(String status, Pageable pageable);

	@Query(value = "SELECT r.* FROM requests r inner join request_announcements ra on r.id = ra.request_id where ra.status = :status and r.title like %:keyword%", nativeQuery = true)
	Page<Request> findAllConflictRequestWithKeyword(String status, String keyword, Pageable pageable);

	Page<Request> findAllByTitleContaining(String keyword, Pageable pageable);

	@Query(value = "select count(*) from requests", nativeQuery = true)
	Integer countAllRequests();

	Integer countRequestsByStatus(Status status);

	@Query(value = "select count(*) from requests r where r.created_date >= ?1 and r.created_date <= ?2", nativeQuery = true)
	Integer countRequestsByStartDateAndEndDate(LocalDateTime startDateTime, LocalDateTime endDateTime);

	@Query(value = "select r.* from requests r inner join request_announcements ra on r.id = ra.request_id where ra.created_date <= ?1 and ra.status = ?2 for update", nativeQuery = true)
	List<Request> findAllExpiredRejectRequest(LocalDateTime localDateTime, String status);
}
