package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.BankCardRequest;
import com.backend.apiserver.bean.request.EWalletRequest;
import com.backend.apiserver.bean.response.BankCardResponse;
import com.backend.apiserver.bean.response.EWalletResponse;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.service.BankCardService;
import com.backend.apiserver.service.EWalletService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@Secured("ROLE_MENTOR")
@RequestMapping("api/mentor/")
public class PaymentController {

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);

	/**
	 * BankCardService
	 */
	private BankCardService bankCardService;

	/**
	 * EWalletService
	 */
	private EWalletService eWalletService;

	/**
	 * Find all mentor bank cards
	 *
	 * @return wrapper of list bank cards
	 */
	@GetMapping("bank-cards")
	public WrapperResponse getMentorBankCards() {
		LOG.info("Start to get list bank card information");
		WrapperResponse wrapperResponse = bankCardService.getBankCards();
		LOG.info("End to get list bank card information");
		return wrapperResponse;
	}

	/**
	 * Find list mentor e-wallets
	 *
	 * @return wrapper of list e-wallets
	 */
	@GetMapping("e-wallets")
	public WrapperResponse getMentorEWallets() {
		LOG.info("Start to get list e-wallet information");
		WrapperResponse wrapperResponse = eWalletService.getEWallets();
		LOG.info("End to get list e-wallet information");
		return wrapperResponse;
	}

	/**
	 * Create new a bank card
	 *
	 * @param bankCardRequest
	 * @return BankCardResponse
	 */
	@PostMapping("bank-card")
	public BankCardResponse createBankCard(@Valid @RequestBody final BankCardRequest bankCardRequest) {
		LOG.info("Start to create bank card with information: ", bankCardRequest.toString());
		BankCardResponse bankCardResponse = bankCardService.createBankCard(bankCardRequest);
		LOG.info("Start to create bank card with information: ", bankCardRequest.toString());
		return bankCardResponse;
	}

	/**
	 * Create new a e-wallet
	 *
	 * @param eWalletRequest
	 * @return EWalletResponse
	 */
	@PostMapping("e-wallet")
	public EWalletResponse createEWallet(@Valid @RequestBody final EWalletRequest eWalletRequest) {
		LOG.info("Start to create e-wallets card with information: ", eWalletRequest.toString());
		EWalletResponse eWalletResponse = eWalletService.createEWallet(eWalletRequest);
		LOG.info("Start to create e-wallets with information: ", eWalletRequest.toString());
		return eWalletResponse;
	}

	/**
	 * Update new bank cards
	 *
	 * @param id
	 * @param bankCardRequest
	 * @return Response
	 * @throws BadRequestException
	 */
	@PutMapping("bank-card/{id}")
	public Response updateBankCard(@PathVariable final Long id,
	                               @Valid @RequestBody final BankCardRequest bankCardRequest) throws BadRequestException {
		try {
			LOG.info("Start to update bank card with id: ", id);
			bankCardService.updateBankCard(id, bankCardRequest);
			LOG.info("End to update bank card with id: ", id);
			return new Response(ResponseMessage.UpdateCardSuccess, id);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.CardIdNotFound, id);
		}
	}

	/**
	 * Update a E-Wallets
	 *
	 * @param id
	 * @param eWalletRequest
	 * @return Response
	 * @throws BadRequestException
	 */
	@PutMapping("e-wallet/{id}")
	public Response updateEWallet(@PathVariable final Long id,
	                              @Valid @RequestBody final EWalletRequest eWalletRequest) throws BadRequestException {
		try {
			LOG.info("Start to update e-wallet with id: ", id);
			eWalletService.updateEWallet(id, eWalletRequest);
			LOG.info("End to update e-wallet with id: ", id);
			return new Response(ResponseMessage.UpdateCardSuccess, id);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.CardIdNotFound, id);
		}
	}

	/**
	 * Delete a bank cards
	 *
	 * @param id id
	 * @return Response
	 * @throws BadRequestException
	 */
	@DeleteMapping("bank-card/{id}")
	public Response deleteBankCard(@PathVariable final Long id) throws BadRequestException {
		try {
			LOG.info("Start to delete bank card with with id: ", id);
			bankCardService.deleteBankCard(id);
			LOG.info("End to delete bank card with with id: ", id);
			return new Response(ResponseMessage.DeleteCardSuccess, id);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.CardIdNotFound, id);
		}
	}

	/**
	 * Delete e wallets
	 *
	 * @param id
	 * @return Response
	 * @throws BadRequestException
	 */
	@DeleteMapping("e-wallet/{id}")
	public Response deleteEWallet(@PathVariable final Long id) throws BadRequestException {
		try {
			LOG.info("Start to delete e-wallet with with id: ", id);
			eWalletService.deleteEWallet(id);
			LOG.info("End to delete e-wallet with with id: ", id);
			return new Response(ResponseMessage.DeleteCardSuccess, id);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.CardIdNotFound, id);
		}
	}
}
