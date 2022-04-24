package com.backend.apiserver.controller;

import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.TransactionHistoryResponse;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.TransactionHistoryService;
import com.backend.apiserver.controller.TransactionHistoryController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class TransactionHistoryControllerTest {

	@InjectMocks
	private TransactionHistoryController transactionHistoryController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private TransactionHistoryService transactionHistoryService;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders
				.standaloneSetup(transactionHistoryController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void getMoneyInHistories() throws Exception {
		TransactionHistoryResponse transactionHistoryResponse = new TransactionHistoryResponse();
		PagingWrapperResponse pagingWrapperResponse = new PagingWrapperResponse(Arrays.asList(transactionHistoryResponse),10);
		when(transactionHistoryService.getMoneyInHistories(1,10)).thenReturn(pagingWrapperResponse);
		mockMvc.perform(get("/api/money-in-history"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)));
	}

	@Test
	public void getMoneyOutHistories() throws Exception {
		TransactionHistoryResponse transactionHistoryResponse = new TransactionHistoryResponse();
		PagingWrapperResponse pagingWrapperResponse = new PagingWrapperResponse(Arrays.asList(transactionHistoryResponse),10);
		when(transactionHistoryService.getMoneyOutHistories(1,10)).thenReturn(pagingWrapperResponse);
		mockMvc.perform(get("/api/money-out-history"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)));
	}

	@Test
	public void getMoneyExchangeHistories() throws Exception {
		TransactionHistoryResponse transactionHistoryResponse = new TransactionHistoryResponse();
		PagingWrapperResponse pagingWrapperResponse = new PagingWrapperResponse(Arrays.asList(transactionHistoryResponse),10);
		when(transactionHistoryService.getMoneyExchangeHistories(1,10)).thenReturn(pagingWrapperResponse);
		mockMvc.perform(get("/api/money-exchange-history"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)));
	}
}