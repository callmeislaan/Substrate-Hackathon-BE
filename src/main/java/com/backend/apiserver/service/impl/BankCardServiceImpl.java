package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.BankCardRequest;
import com.backend.apiserver.bean.response.BankCardResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.entity.BankCard;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.repository.BankCardRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.service.BankCardService;
import com.backend.apiserver.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BankCardServiceImpl implements BankCardService {

	/**
	 * JwtUtils
	 */
	private JwtUtils jwtUtils;

	/**
	 * BankCardRepository
	 */
	private BankCardRepository bankCardRepository;

	/**
	 * MentorRepository
	 */
	private MentorRepository mentorRepository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WrapperResponse getBankCards() {
		Long userId = jwtUtils.getUserId();
		List<BankCard> bankCards = bankCardRepository.findAllByMentorIdAndStatus(userId, Status.ACTIVE);
		return new WrapperResponse(
				bankCards
						.stream()
						.map(bankCard -> entityToResponse(bankCard))
						.collect(Collectors.toList())
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public BankCardResponse createBankCard(BankCardRequest bankCardRequest) {
		Long userId = jwtUtils.getUserId();
		Mentor mentor = mentorRepository.findMentorByUserIdAndStatus(userId, Status.ACTIVE);
		BankCard bankCard = bankCardRepository.findByMentorIdAndAccountNumberAndStatus(userId, bankCardRequest.getAccountNumber(), Status.DELETE);
		mentor.setTotalBankAccount(mentor.getTotalBankAccount() + 1);
		mentorRepository.saveAndFlush(mentor);
		if (Objects.isNull(bankCard)) {
			return entityToResponse(bankCardRepository.saveAndFlush(requestToEntity(mentor, bankCardRequest)));
		}
		bankCard.setStatus(Status.ACTIVE);
		bankCardRepository.saveAndFlush(bankCard);
		return entityToResponse(bankCard);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public void deleteBankCard(Long id) throws NotFoundException {
		Long userId = jwtUtils.getUserId();
		BankCard bankCard = bankCardRepository.findByIdAndMentorIdAndStatus(id, userId, Status.ACTIVE);
		if (Objects.isNull(bankCard)) {
			throw new NotFoundException("Bank Card with this id is not exist: " + id);
		}
		bankCard.setStatus(Status.DELETE);
		Mentor mentor = mentorRepository.findMentorByUserIdAndStatus(bankCard.getMentor().getId(), Status.ACTIVE);
		mentor.setTotalBankAccount(mentor.getTotalBankAccount() - 1);
		mentorRepository.saveAndFlush(mentor);
		bankCardRepository.saveAndFlush(bankCard);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public void updateBankCard(Long id, BankCardRequest bankCardRequest) throws NotFoundException {
		Long userId = jwtUtils.getUserId();
		BankCard bankCard = bankCardRepository.findByIdAndMentorIdAndStatus(id, userId, Status.ACTIVE);
		if (Objects.isNull(bankCard)) {
			throw new NotFoundException("Bank Card with this id is not exist: " + id);
		}
		bankCard.setHolderName(bankCardRequest.getHolderName());
		bankCard.setAccountNumber(bankCardRequest.getAccountNumber());
		bankCard.setBranch(bankCardRequest.getBranch());
		bankCard.setBank(bankCardRequest.getBank());
		bankCardRepository.saveAndFlush(bankCard);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BankCardResponse findBankCard(Long id) throws NotFoundException {
		Long userId = jwtUtils.getUserId();
		BankCard bankCard = bankCardRepository.findByIdAndMentorIdAndStatus(id, userId, Status.ACTIVE);
		if (Objects.isNull(bankCard)) {
			throw new NotFoundException("Bank Card with this id is not exist: " + id);
		}
		return entityToResponse(bankCard);
	}

	private BankCard requestToEntity(Mentor mentor, BankCardRequest bankCardRequest) {
		BankCard bankCard = new BankCard();
		bankCard.setAccountNumber(bankCardRequest.getAccountNumber());
		bankCard.setBank(bankCardRequest.getBank());
		bankCard.setBranch(bankCardRequest.getBranch());
		bankCard.setHolderName(bankCardRequest.getHolderName());
		bankCard.setMentor(mentor);
		bankCard.setStatus(Status.ACTIVE);

		mentor.getBankCards().add(bankCard);
		return bankCard;
	}

	private BankCardResponse entityToResponse(BankCard bankCard) {
		BankCardResponse bankCardResponse = new BankCardResponse();
		bankCardResponse.setId(bankCard.getId());
		bankCardResponse.setAccountNumber(bankCard.getAccountNumber());
		bankCardResponse.setBank(bankCard.getBank());
		bankCardResponse.setBranch(bankCard.getBranch());
		bankCardResponse.setHolderName(bankCard.getHolderName());
		return bankCardResponse;
	}
}
