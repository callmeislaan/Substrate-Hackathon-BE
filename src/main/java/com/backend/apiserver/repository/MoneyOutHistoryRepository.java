package com.backend.apiserver.repository;

import com.backend.apiserver.entity.MoneyOutHistory;
import com.backend.apiserver.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;

public interface MoneyOutHistoryRepository extends JpaRepository<MoneyOutHistory, Long>, PagingAndSortingRepository<MoneyOutHistory, Long> {
	/**
	 * Get all money out history with mentorId and pageable
	 *
	 * @param mentorId
	 * @param pageable
	 * @return Page MoneyOutHistory
	 */
	Page<MoneyOutHistory> findAllByMentorId(Long mentorId, Pageable pageable);

	@Query(value = "select count(*) from money_out_histories", nativeQuery = true)
	Integer countAllMoneyOutHistories();

	@Query(value = "select count(*) from money_out_histories r where r.created_date >= ?1 and r.created_date <= ?2", nativeQuery = true)
	Integer countMoneyOutByStartDateAndEndDate(LocalDateTime startDateTime, LocalDateTime endDateTime);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	MoneyOutHistory findByIdAndStatus(Long id, Status status);
}
