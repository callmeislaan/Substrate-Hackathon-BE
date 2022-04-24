package com.backend.apiserver.repository;

import com.backend.apiserver.entity.BankCard;
import com.backend.apiserver.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankCardRepository extends JpaRepository<BankCard, Long> {

	/**
	 * Get mentor bank card information
	 *
	 * @param mentorId mentor id
	 * @param id       bank card id
	 * @param status   only active status
	 * @return BankCard
	 */
	BankCard findByIdAndMentorIdAndStatus(Long id, Long mentorId, Status status);

	/**
	 * Get list bank cards created by mentor
	 *
	 * @param mentorId mentor id
	 * @param status   only active status
	 * @return list of bankcards
	 */
	List<BankCard> findAllByMentorIdAndStatus(Long mentorId, Status status);

	/**
	 * Check if bank card still active
	 *
	 * @param id
	 * @param status
	 * @return
	 */
	boolean existsByIdAndStatus(Long id, Status status);

	BankCard findByMentorIdAndAccountNumberAndStatus(Long mentorId, String accountNumber, Status status);
}
