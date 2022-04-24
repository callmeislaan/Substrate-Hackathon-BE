package com.backend.apiserver.repository;

import com.backend.apiserver.entity.MoneyInHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;

public interface MoneyInHistoryRepository extends JpaRepository<MoneyInHistory, Long>, PagingAndSortingRepository<MoneyInHistory, Long> {
	/**
	 * Get all money in history with userId and pageable
	 *
	 * @param userId
	 * @param pageable
	 * @return Page MoneyInHistory
	 */
	Page<MoneyInHistory> findAllByUserId(Long userId, Pageable pageable);

	@Query(value = "select count(*) from money_in_histories", nativeQuery = true)
	Integer countAllMoneyInHistories();

	@Query(value = "select count(*) from money_in_histories r where r.created_date >= ?1 and r.created_date <= ?2", nativeQuery = true)
	Integer countMoneyInByStartDateAndEndDate(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
