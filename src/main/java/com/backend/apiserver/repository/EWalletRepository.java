package com.backend.apiserver.repository;

import com.backend.apiserver.entity.EWallet;
import com.backend.apiserver.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EWalletRepository extends JpaRepository<EWallet, Long> {

	/**
	 * Find e wallet create by a mentor
	 *
	 * @param id
	 * @param mentorId
	 * @param status
	 * @return
	 */
	EWallet findByIdAndMentorIdAndStatus(Long id, Long mentorId, Status status);

	/**
	 * Get list e-wallets created by mentor
	 *
	 * @param mentorId mentor id
	 * @param status   only active status
	 * @return list of e-wallets
	 */
	List<EWallet> findAllByMentorIdAndStatus(Long mentorId, Status status);

	/**
	 * Check if e-wallet still active
	 *
	 * @param id
	 * @param status
	 * @return
	 */
	boolean existsByIdAndStatus(Long id, Status status);

	EWallet findByMentorIdAndHolderNameAndPhoneAndStatus(Long mentorId, String holderName, String phone, Status status);
}
