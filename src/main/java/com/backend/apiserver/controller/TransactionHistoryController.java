package com.backend.apiserver.controller;

import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.service.TransactionHistoryService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TransactionHistoryController {

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(TransactionHistoryController.class);

	/**
	 * TransactionHistoryService
	 */
	private TransactionHistoryService transactionHistoryService;

	/**
	 * Find all money in history
	 *
	 * @return wrapper of list money in history
	 */
	@GetMapping("api/money-in-history")
	public PagingWrapperResponse getMoneyInHistories(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		LOG.info("Start to get list money in history information");
		PagingWrapperResponse pagingWrapperResponse = transactionHistoryService.getMoneyInHistories(page, size);
		LOG.info("End to get list money in history information");
		return pagingWrapperResponse;
	}

	/**
	 * Find all money out history
	 *
	 * @return wrapper of list money out history
	 */
	@Secured("ROLE_MENTOR")
	@GetMapping("api/money-out-history")
	public PagingWrapperResponse getMoneyOutHistories(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		LOG.info("Start to get list money out history information");
		PagingWrapperResponse pagingWrapperResponse = transactionHistoryService.getMoneyOutHistories(page, size);
		LOG.info("End to get list money out history information");
		return pagingWrapperResponse;
	}

	/**
	 * Find all money exchange history
	 *
	 * @return wrapper of list money exchange history
	 */
	@GetMapping("api/money-exchange-history")
	public PagingWrapperResponse getMoneyExchangeHistories(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		LOG.info("Start to get list money exchange history information");
		PagingWrapperResponse pagingWrapperResponse = transactionHistoryService.getMoneyExchangeHistories(page, size);
		LOG.info("End to get list money exchange history information");
		return pagingWrapperResponse;
	}

}
