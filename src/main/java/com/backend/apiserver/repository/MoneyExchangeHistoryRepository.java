package com.backend.apiserver.repository;

import com.backend.apiserver.entity.MoneyExchangeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;

public interface MoneyExchangeHistoryRepository extends JpaRepository<MoneyExchangeHistory, Long>, PagingAndSortingRepository<MoneyExchangeHistory, Long> {

	/**
	 * Get income statistic weekly, monthly, daily
	 *
	 * @param dateTime weekly, daily, monthly time
	 * @param mentorId id of mentor
	 * @return total income
	 */
	@Query("select COALESCE(sum(m.amount), 0) from MoneyExchangeHistory m where m.lastModifiedDate >= ?1 and m.mentor.id = ?2")
	int getIncomeStatistic(LocalDateTime dateTime, Long mentorId);

	/**
	 * Get all money exchange history with mentorId, userId, pageable
	 *
	 * @param mentorId
	 * @param userId
	 * @param pageable
	 * @return Page MoneyExchangeHistory
	 */
	Page<MoneyExchangeHistory> findAllByMentorIdOrUserId(Long mentorId, Long userId, Pageable pageable);

	@Query(value = "select count(*) from money_exchange_histories", nativeQuery = true)
	Integer countAllMoneyExchangeHistories();

	@Query(value = "select count(*) from money_exchange_histories r where r.created_date >= ?1 and r.created_date <= ?2", nativeQuery = true)
	Integer countMoneyExchangeByStartDateAndEndDate(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
