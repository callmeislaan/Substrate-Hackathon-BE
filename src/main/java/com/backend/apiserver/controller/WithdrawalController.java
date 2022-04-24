package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.WithdrawRequest;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.service.WithdrawalService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;


@RestController
@AllArgsConstructor
@Secured("ROLE_MENTOR")
public class WithdrawalController {

	private static final Logger LOG = LoggerFactory.getLogger(WithdrawalController.class);

	private WithdrawalService withdrawalService;

	@PostMapping("api/mentor/withdrawal/bank-card/{cardId}")
	public Response withdrawWithBankCard(@PathVariable final Long cardId, @Valid @RequestBody WithdrawRequest withdrawRequest) throws BadRequestException, MessagingException, IOException, TemplateException {
		try {
			LOG.info("Start to withdraw user money to bank card with amount: " + withdrawRequest.getAmount());
			withdrawalService.withdrawWithBankCard(cardId, withdrawRequest);
			LOG.info("End to withdraw user money to bank card with amount: " + withdrawRequest.getAmount());
			return new Response(ResponseMessage.RequestWithdrawalSuccess);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.CardIdNotFound, cardId);
		} catch (MoneyRelatedException e) {
			throw new BadRequestException(ResponseMessage.WithdrawMoneyExceedCurrentValueException);
		}
	}

	@PostMapping("api/mentor/withdrawal/e-wallet/{eWalletId}")
	public Response withdrawWithEWallet(@PathVariable final Long eWalletId, @Valid @RequestBody WithdrawRequest withdrawRequest) throws BadRequestException, MessagingException, IOException, TemplateException {
		try {
			LOG.info("Start to withdraw user money to bank card with amount: " + withdrawRequest.getAmount());
			withdrawalService.withdrawWithEWallet(eWalletId, withdrawRequest);
			LOG.info("End to withdraw user money to bank card with amount: " + withdrawRequest.getAmount());
			return new Response(ResponseMessage.RequestWithdrawalSuccess);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.CardIdNotFound, eWalletId);
		} catch (MoneyRelatedException e) {
			throw new BadRequestException(ResponseMessage.WithdrawMoneyExceedCurrentValueException);
		}
	}
}
