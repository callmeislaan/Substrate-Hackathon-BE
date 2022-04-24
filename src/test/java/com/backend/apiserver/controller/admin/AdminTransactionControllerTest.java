package com.backend.apiserver.controller.admin;

import com.backend.apiserver.bean.request.AnestCardRequest;
import com.backend.apiserver.bean.request.MoneyDetailRequest;
import com.backend.apiserver.bean.request.MoneyRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.PaymentInfoResponse;
import com.backend.apiserver.bean.response.TransactionHistoryResponse;
import com.backend.apiserver.controller.admin.AdminTransactionController;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.TransactionService;
import com.backend.apiserver.utils.FormatUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class AdminTransactionControllerTest {

	@InjectMocks
	AdminTransactionController adminTransactionController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private TransactionService transactionService;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(adminTransactionController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void createMoneyIn() throws Exception {
		MoneyDetailRequest moneyDetailRequest = new MoneyDetailRequest();
		MoneyRequest moneyRequest = new MoneyRequest();
		moneyRequest.setMoneyDetailRequests(Arrays.asList(moneyDetailRequest));
		mockMvc.perform(post("/api/admin/add-money")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(moneyRequest)))
				.andExpect(status().isOk());
	}

	@Test
	public void generateAnestCard() throws Exception {
		AnestCardRequest anestCardRequest = new AnestCardRequest();
		anestCardRequest.setValue(1000);
		anestCardRequest.setStatus(true);
		mockMvc.perform(post("/api/admin/anest-card")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(anestCardRequest)))
				.andExpect(status().isOk());
	}

	@Test
	public void updateAnestCard() throws Exception {
		AnestCardRequest anestCardRequest = new AnestCardRequest();
		anestCardRequest.setValue(1000);
		anestCardRequest.setStatus(true);
		mockMvc.perform(put("/api/admin/anest-card/15")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(anestCardRequest)))
				.andExpect(status().isOk());
	}

	@Test
	public void findAnestCards() throws Exception {
		when(transactionService.findAnestCards(1,10))
				.thenReturn(new PagingWrapperResponse(Arrays.asList(1),1));
		mockMvc.perform(get("/api/admin/anest-cards"))
				.andExpect(status().isOk());
	}

	@Test
	public void disableAnestCard() throws Exception {
		mockMvc.perform(delete("/api/admin/anest-card/15"))
				.andExpect(status().isOk());
	}

	@Test
	public void disableAnestCard_throwNotFoundException() throws Exception {
		doThrow(new NotFoundException("")).when(transactionService).disableAnestCard(15L);
		mockMvc.perform(delete("/api/admin/anest-card/15"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void getMoneyInTransactions() throws Exception {
		TransactionHistoryResponse transactionHistoryResponse = new TransactionHistoryResponse();
		PagingWrapperResponse pagingWrapperResponse = new PagingWrapperResponse(Arrays.asList(transactionHistoryResponse),10);
		when(transactionService.getMoneyInTransactions(1,10)).thenReturn(pagingWrapperResponse);
		mockMvc.perform(get("/api/admin/money-in-transactions"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)));
	}

	@Test
	public void getMoneyOutTransactions() throws Exception {
		TransactionHistoryResponse transactionHistoryResponse = new TransactionHistoryResponse();
		PagingWrapperResponse pagingWrapperResponse = new PagingWrapperResponse(Arrays.asList(transactionHistoryResponse),10);
		when(transactionService.getMoneyOutTransactions(1,10)).thenReturn(pagingWrapperResponse);
		mockMvc.perform(get("/api/admin/money-out-transactions"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)));
	}

	@Test
	public void getMoneyExchangeTransactions() throws Exception {
		TransactionHistoryResponse transactionHistoryResponse = new TransactionHistoryResponse();
		PagingWrapperResponse pagingWrapperResponse = new PagingWrapperResponse(Arrays.asList(transactionHistoryResponse),10);
		when(transactionService.getMoneyExchangeTransactions(1,10)).thenReturn(pagingWrapperResponse);
		mockMvc.perform(get("/api/admin/money-exchange-transactions"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)));
	}

	@Test
	public void getPaymentMethodInfo() throws Exception {
		PaymentInfoResponse paymentInfoResponse =new PaymentInfoResponse();
		paymentInfoResponse.setEWalletName("MOMO");
		paymentInfoResponse.setPhone("0969563145");
		when(transactionService.getPaymentMethodInfo(15L)).thenReturn(paymentInfoResponse);
		mockMvc.perform(get("/api/admin/payment-info/15"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.eWalletName", is("MOMO")))
				.andExpect(jsonPath("$.phone", is("0969563145")));
	}

	@Test
	public void changeMoneyOutStatus() throws Exception {
		mockMvc.perform(put("/api/admin/withdrawal/15"))
				.andExpect(status().isOk());
	}

	@Test
	public void changeMoneyOutStatus_ThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException("")).when(transactionService).changeMoneyOutStatus(15L);
		mockMvc.perform(put("/api/admin/withdrawal/15"))
				.andExpect(status().isBadRequest());
	}
}