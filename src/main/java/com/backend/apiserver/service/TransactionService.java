package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.AnestCardRequest;
import com.backend.apiserver.bean.request.MoneyRequest;
import com.backend.apiserver.bean.response.AnestCardResponse;
import com.backend.apiserver.bean.response.MoneyFlowResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.PaymentInfoResponse;
import com.backend.apiserver.exception.NotFoundException;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;

public interface TransactionService {

	MoneyFlowResponse createMoneyIn(MoneyRequest moneyRequest) throws NotFoundException, MessagingException, IOException, TemplateException;

	AnestCardResponse generateAnestCard(AnestCardRequest anestCardRequest);

	AnestCardResponse updateAnestCard(Long id, AnestCardRequest anestCardRequest) throws NotFoundException;

	PagingWrapperResponse findAnestCards(Integer page, Integer size);

	void disableAnestCard(Long id) throws NotFoundException;

	PagingWrapperResponse getMoneyInTransactions(Integer page, Integer size);

	PagingWrapperResponse getMoneyOutTransactions(Integer page, Integer size);

	PagingWrapperResponse getMoneyExchangeTransactions(Integer page, Integer size);

	PaymentInfoResponse getPaymentMethodInfo(Long transactionId) throws NotFoundException;

	void changeMoneyOutStatus(Long transactionId) throws NotFoundException, MessagingException, IOException, TemplateException;
}
