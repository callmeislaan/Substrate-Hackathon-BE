package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.MailRequest;
import com.backend.apiserver.bean.request.WithdrawRequest;
import com.backend.apiserver.entity.BankCard;
import com.backend.apiserver.entity.EWallet;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MoneyOutHistory;
import com.backend.apiserver.entity.PaymentMethod;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.repository.BankCardRepository;
import com.backend.apiserver.repository.EWalletRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MoneyOutHistoryRepository;
import com.backend.apiserver.service.EmailSenderService;
import com.backend.apiserver.service.WithdrawalService;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.FormatUtils;
import com.backend.apiserver.utils.JwtUtils;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {

	/**
	 * JwtUtils
	 */
	private JwtUtils jwtUtils;


	/**
	 * UserSummaryRepository
	 */
	private MentorRepository mentorRepository;

	/**
	 * MoneyOutHistoryRepository
	 */
	private MoneyOutHistoryRepository moneyOutHistoryRepository;

	private BankCardRepository bankCardRepository;

	private EWalletRepository eWalletRepository;

	private EmailSenderService emailSenderService;

	@Override
	@AnestTransactional
	public void withdrawWithEWallet(Long eWalletId, WithdrawRequest withdrawRequest) throws NotFoundException, MoneyRelatedException, MessagingException, IOException, TemplateException {
		Long userId = jwtUtils.getUserId();
		Mentor mentor = mentorRepository.findMentorByUserIdAndStatus(userId, Status.ACTIVE);

		if (withdrawRequest.getAmount() > mentor.getTotalMoneyCurrent()) {
			throw new MoneyRelatedException("Your request withdraw money amount exceeded balance in wallet");
		}

		EWallet eWallet = eWalletRepository.findByIdAndMentorIdAndStatus(eWalletId, userId, Status.ACTIVE);
		if (Objects.isNull(eWallet)) {
			throw new NotFoundException("This e-wallet is not exist in DB");
		}

		//add new record to money out history with status pending
		MoneyOutHistory moneyOutHistory = new MoneyOutHistory();
		moneyOutHistory.setMentor(mentor);
		moneyOutHistory.setPaymentMethodId(eWalletId);
		moneyOutHistory.setPaymentMethod(PaymentMethod.E_WALLET);
		moneyOutHistory.setAmount(withdrawRequest.getAmount());
		moneyOutHistory.setStatus(Status.PENDING);
		moneyOutHistoryRepository.saveAndFlush(moneyOutHistory);

		//update total money on user summary
		mentor.setTotalMoneyCurrent(mentor.getTotalMoneyCurrent() - withdrawRequest.getAmount());
		mentor.setTotalMoneyOut(mentor.getTotalMoneyOut() + withdrawRequest.getAmount());
		mentor.getMoneyOutHistories().add(moneyOutHistory);
		User mentorUser = mentor.getUser();

		Map<String, Object> params = new HashMap<>();
		params.put("fullName", mentorUser.getUserDetail().getFullName());
		params.put("pendingMessage", Constants.PENDING_MESSAGE);

		params.put("holderName", eWallet.getHolderName());
		params.put("eWallet", eWallet.getEWalletName());
		params.put("phone", eWallet.getPhone());
		params.put("amount", withdrawRequest.getAmount());
		params.put("status", Constants.PENDING_STATUS);
		params.put("announcement", Constants.PENDING_ANNOUNCEMENT);
		params.put("color", Constants.PENDING_COLOR);

		//send mail user
		MailRequest mailRequest = new MailRequest(
				FormatUtils.makeArray(mentorUser.getEmail()),
				params,
				MailRequest.TEMPLATE_E_WALLET,
				MailRequest.TITLE_MONEY_OUT
		);
		emailSenderService.sendEmailTemplate(mailRequest);
	}

	@Override
	@AnestTransactional
	public void withdrawWithBankCard(Long cardId, WithdrawRequest withdrawRequest) throws NotFoundException, MoneyRelatedException, MessagingException, IOException, TemplateException {
		Long userId = jwtUtils.getUserId();
		Mentor mentor = mentorRepository.findMentorByUserIdAndStatus(userId, Status.ACTIVE);
		if (withdrawRequest.getAmount() > mentor.getTotalMoneyCurrent()) {
			throw new MoneyRelatedException("Your request withdraw money amount exceeded balance in wallet");
		}

		BankCard bankCard = bankCardRepository.findByIdAndMentorIdAndStatus(cardId, userId, Status.ACTIVE);
		if (Objects.isNull(bankCard)) {
			throw new NotFoundException("This card is not exist in DB");
		}

		//add new record to money out history with status pending
		MoneyOutHistory moneyOutHistory = new MoneyOutHistory();
		moneyOutHistory.setMentor(mentor);
		moneyOutHistory.setPaymentMethodId(cardId);
		moneyOutHistory.setPaymentMethod(PaymentMethod.BANK_CARD);
		moneyOutHistory.setAmount(withdrawRequest.getAmount());
		moneyOutHistory.setStatus(Status.PENDING);
		moneyOutHistoryRepository.saveAndFlush(moneyOutHistory);

		//update total money on user summary
		mentor.setTotalMoneyCurrent(mentor.getTotalMoneyCurrent() - withdrawRequest.getAmount());
		mentor.setTotalMoneyOut(mentor.getTotalMoneyOut() + withdrawRequest.getAmount());
		mentor.getMoneyOutHistories().add(moneyOutHistory);
		mentorRepository.saveAndFlush(mentor);

		User mentorUser = mentor.getUser();
		UserDetail userDetail = mentorUser.getUserDetail();

		Map<String, Object> params = new HashMap<>();
		params.put("fullName", userDetail.getFullName());
		params.put("pendingMessage", Constants.PENDING_MESSAGE);

		params.put("holderName", bankCard.getHolderName());
		params.put("accountNumber", bankCard.getAccountNumber());
		params.put("bank", bankCard.getBank());
		params.put("branch", bankCard.getBranch());
		params.put("amount", withdrawRequest.getAmount());
		params.put("status", Constants.PENDING_STATUS);
		params.put("announcement", Constants.PENDING_ANNOUNCEMENT);
		params.put("color", Constants.PENDING_COLOR);

		//send mail user
		MailRequest mailRequest = new MailRequest(
				FormatUtils.makeArray(mentorUser.getEmail()),
				params,
				MailRequest.TEMPLATE_BANK_CARD,
				MailRequest.TITLE_MONEY_OUT
		);
		emailSenderService.sendEmailTemplate(mailRequest);
	}
}
