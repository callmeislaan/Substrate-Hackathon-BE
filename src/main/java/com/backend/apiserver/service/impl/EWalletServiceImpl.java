package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.EWalletRequest;
import com.backend.apiserver.bean.response.EWalletResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.entity.EWallet;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.repository.EWalletRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.service.EWalletService;
import com.backend.apiserver.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EWalletServiceImpl implements EWalletService {

	/**
	 * JwtUtils
	 */
	private JwtUtils jwtUtils;

	/**
	 * BankCardRepository
	 */
	private EWalletRepository eWalletRepository;

	/**
	 * MentorRepository
	 */
	private MentorRepository mentorRepository;

	@Override
	public WrapperResponse getEWallets() {
		Long userId = jwtUtils.getUserId();
		List<EWallet> eWallets = eWalletRepository.findAllByMentorIdAndStatus(userId, Status.ACTIVE);
		return new WrapperResponse(
				eWallets
						.stream()
						.map(eWallet -> entityToResponse(eWallet))
						.collect(Collectors.toList())
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public EWalletResponse createEWallet(EWalletRequest eWalletRequest) {
		Long userId = jwtUtils.getUserId();
		Mentor mentor = mentorRepository.findMentorByUserIdAndStatus(userId, Status.ACTIVE);
		EWallet eWallet = eWalletRepository
				.findByMentorIdAndHolderNameAndPhoneAndStatus(userId, eWalletRequest.getHolderName(), eWalletRequest.getPhone(), Status.DELETE);
		mentor.setTotalEWallet(mentor.getTotalEWallet() + 1);
		mentorRepository.saveAndFlush(mentor);
		if (Objects.isNull(eWallet)) {
			return entityToResponse(eWalletRepository.saveAndFlush(requestToEntity(mentor, eWalletRequest)));
		}
		eWallet.setStatus(Status.ACTIVE);
		eWalletRepository.saveAndFlush(eWallet);
		return entityToResponse(eWallet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public void deleteEWallet(Long id) throws NotFoundException {
		Long userId = jwtUtils.getUserId();
		EWallet eWallet = eWalletRepository.findByIdAndMentorIdAndStatus(id, userId, Status.ACTIVE);
		if (Objects.isNull(eWallet)) {
			throw new NotFoundException("E-Wallet with this id is not exist: " + id);
		}
		eWallet.setStatus(Status.DELETE);
		Mentor mentor = mentorRepository.findMentorByUserIdAndStatus(eWallet.getMentor().getId(), Status.ACTIVE);
		mentor.setTotalEWallet(mentor.getTotalEWallet() - 1);
		mentorRepository.saveAndFlush(mentor);
		eWalletRepository.saveAndFlush(eWallet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public void updateEWallet(Long id, EWalletRequest eWalletRequest) throws NotFoundException {
		Long userId = jwtUtils.getUserId();
		EWallet eWallet = eWalletRepository.findByIdAndMentorIdAndStatus(id, userId, Status.ACTIVE);
		if (Objects.isNull(eWallet)) {
			throw new NotFoundException("E-Wallet with this id is not exist: " + id);
		}
		eWallet.setHolderName(eWalletRequest.getHolderName());
		eWallet.setPhone(eWalletRequest.getPhone());
		eWallet.setEWalletName(eWalletRequest.getEWalletName());

		eWalletRepository.saveAndFlush(eWallet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EWalletResponse findEWallet(Long id) throws NotFoundException {
		Long userId = jwtUtils.getUserId();
		EWallet eWallet = eWalletRepository.findByIdAndMentorIdAndStatus(id, userId, Status.ACTIVE);
		if (Objects.isNull(eWallet)) {
			throw new NotFoundException("E-Wallet with this id is not exist: " + id);
		}
		return entityToResponse(eWallet);
	}

	private EWallet requestToEntity(Mentor mentor, EWalletRequest eWalletRequest) {
		EWallet eWallet = new EWallet();
		eWallet.setEWalletName(eWalletRequest.getEWalletName());
		eWallet.setPhone(eWalletRequest.getPhone());
		eWallet.setHolderName(eWalletRequest.getHolderName());
		eWallet.setStatus(Status.ACTIVE);

		eWallet.setMentor(mentor);
		return eWallet;
	}

	private EWalletResponse entityToResponse(EWallet eWallet) {
		EWalletResponse eWalletResponse = new EWalletResponse();
		eWalletResponse.setId(eWallet.getId());
		eWalletResponse.setEWalletName(eWallet.getEWalletName());
		eWalletResponse.setPhone(eWallet.getPhone());
		eWalletResponse.setHolderName(eWallet.getHolderName());

		return eWalletResponse;
	}
}
