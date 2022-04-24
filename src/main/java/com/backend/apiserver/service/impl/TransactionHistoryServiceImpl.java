package com.backend.apiserver.service.impl;

import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.TransactionHistoryResponse;
import com.backend.apiserver.entity.MoneyExchangeHistory;
import com.backend.apiserver.entity.MoneyInHistory;
import com.backend.apiserver.entity.MoneyOutHistory;
import com.backend.apiserver.repository.MoneyExchangeHistoryRepository;
import com.backend.apiserver.repository.MoneyInHistoryRepository;
import com.backend.apiserver.repository.MoneyOutHistoryRepository;
import com.backend.apiserver.service.TransactionHistoryService;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.DateTimeUtils;
import com.backend.apiserver.utils.FormatUtils;
import com.backend.apiserver.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

	/**
	 * JwtUtils
	 */
	private JwtUtils jwtUtils;

	/**
	 * MoneyInHistoryRepository
	 */
	private MoneyInHistoryRepository moneyInHistoryRepository;

	/**
	 * MoneyOutHistoryRepository
	 */
	private MoneyOutHistoryRepository moneyOutHistoryRepository;

	/**
	 * MoneyExchangeHistoryRepository
	 */
	private MoneyExchangeHistoryRepository moneyExchangeHistoryRepository;

	@Override
	public PagingWrapperResponse getMoneyInHistories(Integer page, Integer size) {
		Long userId = jwtUtils.getUserId();
		Page<MoneyInHistory> moneyInHistories = moneyInHistoryRepository.findAllByUserId(userId, PageRequest.of(page - 1, size, Sort.by("id").descending()));
		return new PagingWrapperResponse(moneyInHistories.getContent().stream().map(this::moneyInHistoryToResponse).collect(Collectors.toList()), moneyInHistories.getTotalElements());
	}

	@Override
	public PagingWrapperResponse getMoneyOutHistories(Integer page, Integer size) {
		Long userId = jwtUtils.getUserId();
		Page<MoneyOutHistory> moneyOutHistories = moneyOutHistoryRepository.findAllByMentorId(userId, PageRequest.of(page - 1, size, Sort.by("id").descending()));
		return new PagingWrapperResponse(moneyOutHistories.getContent().stream().map(this::moneyOutHistoryToResponse).collect(Collectors.toList()), moneyOutHistories.getTotalElements());
	}

	@Override
	public PagingWrapperResponse getMoneyExchangeHistories(Integer page, Integer size) {
		Long userId = jwtUtils.getUserId();
		Page<MoneyExchangeHistory> moneyExchangeHistories = moneyExchangeHistoryRepository.findAllByMentorIdOrUserId(userId, userId, PageRequest.of(page - 1, size, Sort.by("id").descending()));
		return new PagingWrapperResponse(moneyExchangeHistories.getContent().stream().map(moneyExchangeHistory -> moneyExchangeHistoryToResponse(moneyExchangeHistory, userId)).collect(Collectors.toList()), moneyExchangeHistories.getTotalElements());
	}

	private TransactionHistoryResponse moneyInHistoryToResponse(MoneyInHistory moneyInHistory) {
		TransactionHistoryResponse transactionHistoryResponse = new TransactionHistoryResponse();
		transactionHistoryResponse.setTransactionId(FormatUtils.transactionIdFormatter(moneyInHistory.getId()));
		transactionHistoryResponse.setCreatedDate(DateTimeUtils.toCurrentTimeMillis(moneyInHistory.getCreatedDate()));
		transactionHistoryResponse.setPaymentMethod(moneyInHistory.getPaymentMethod().getMethodName());
		transactionHistoryResponse.setAmount(moneyInHistory.getAmount());
		transactionHistoryResponse.setStatus(moneyInHistory.getStatus().toString());
		return transactionHistoryResponse;
	}

	private TransactionHistoryResponse moneyOutHistoryToResponse(MoneyOutHistory moneyOutHistory) {
		TransactionHistoryResponse transactionHistoryResponse = new TransactionHistoryResponse();
		transactionHistoryResponse.setTransactionId(FormatUtils.transactionIdFormatter(moneyOutHistory.getId()));
		transactionHistoryResponse.setCreatedDate(DateTimeUtils.toCurrentTimeMillis(moneyOutHistory.getCreatedDate()));
		transactionHistoryResponse.setPaymentMethod(moneyOutHistory.getPaymentMethod().getMethodName());
		transactionHistoryResponse.setAmount(moneyOutHistory.getAmount());
		transactionHistoryResponse.setStatus(moneyOutHistory.getStatus().toString());
		return transactionHistoryResponse;
	}

	private TransactionHistoryResponse moneyExchangeHistoryToResponse(MoneyExchangeHistory moneyExchangeHistory, Long userId) {
		boolean check = moneyExchangeHistory.getUser().getId().equals(userId);
		TransactionHistoryResponse transactionHistoryResponse = new TransactionHistoryResponse();
		transactionHistoryResponse.setTransactionId(FormatUtils.transactionIdFormatter(moneyExchangeHistory.getId()));
		transactionHistoryResponse.setCreatedDate(DateTimeUtils.toCurrentTimeMillis(moneyExchangeHistory.getCreatedDate()));
		transactionHistoryResponse.setPaymentMethod(check ? Constants.PAYMENT : Constants.RECEIVE);
		transactionHistoryResponse.setAmount(moneyExchangeHistory.getAmount());
		transactionHistoryResponse.setStatus(moneyExchangeHistory.getStatus().toString());
		return transactionHistoryResponse;
	}
}
