package com.backend.apiserver.controller.admin;

import com.backend.apiserver.bean.request.AnestCardRequest;
import com.backend.apiserver.bean.request.MoneyRequest;
import com.backend.apiserver.bean.response.AnestCardResponse;
import com.backend.apiserver.bean.response.MoneyFlowResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.PaymentInfoResponse;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.service.TransactionService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@AllArgsConstructor
@Secured("ROLE_ADMIN")
@RequestMapping("api/admin/")
public class AdminTransactionController {

	private TransactionService transactionService;

	@PostMapping("add-money")
	public MoneyFlowResponse createMoneyIn(@Valid @RequestBody MoneyRequest moneyRequest) throws MessagingException, TemplateException, NotFoundException, IOException {
		return transactionService.createMoneyIn(moneyRequest);
	}

	@PostMapping("anest-card")
	public AnestCardResponse generateAnestCard(@Valid @RequestBody AnestCardRequest anestCardRequest) {
		return transactionService.generateAnestCard(anestCardRequest);
	}

	@PutMapping("anest-card/{id}")
	public AnestCardResponse updateAnestCard(@PathVariable Long id, @Valid @RequestBody AnestCardRequest anestCardRequest) throws NotFoundException {
		return transactionService.updateAnestCard(id, anestCardRequest);
	}

	@GetMapping("anest-cards")
	public PagingWrapperResponse findAnestCards(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		return transactionService.findAnestCards(page, size);
	}

	@DeleteMapping("anest-card/{id}")
	public Response disableAnestCard(@PathVariable final Long id) throws BadRequestException {
		try {
			transactionService.disableAnestCard(id);
			return new Response(ResponseMessage.PerformOperationSuccess);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.DataNotFoundException);
		}
	}

	@GetMapping("money-in-transactions")
	public PagingWrapperResponse getMoneyInTransactions(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		return transactionService.getMoneyInTransactions(page, size);
	}

	@GetMapping("money-out-transactions")
	public PagingWrapperResponse getMoneyOutTransactions(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		return transactionService.getMoneyOutTransactions(page, size);
	}

	@GetMapping("money-exchange-transactions")
	public PagingWrapperResponse getMoneyExchangeTransactions(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		return transactionService.getMoneyExchangeTransactions(page, size);
	}

	@GetMapping("payment-info/{transactionId}")
	public PaymentInfoResponse getPaymentMethodInfo(@PathVariable Long transactionId) throws NotFoundException {
		return transactionService.getPaymentMethodInfo(transactionId);
	}

	@PutMapping("withdrawal/{transactionId}")
	public Response changeMoneyOutStatus(@PathVariable final Long transactionId) throws BadRequestException, MessagingException, IOException, TemplateException {
		try {
			transactionService.changeMoneyOutStatus(transactionId);
			return new Response(ResponseMessage.PerformOperationSuccess);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.DataNotFoundException);
		}

	}
}
