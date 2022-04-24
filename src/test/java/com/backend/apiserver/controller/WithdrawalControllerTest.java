package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.WithdrawRequest;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.WithdrawalService;
import com.backend.apiserver.controller.WithdrawalController;
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

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class WithdrawalControllerTest {

	@InjectMocks
	private WithdrawalController withdrawalController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private WithdrawalService withdrawalService;

	private WithdrawRequest request;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders
				.standaloneSetup(withdrawalController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
		request = new WithdrawRequest();
	}

	@Test
	public void withdrawWithBankCard_Success() throws Exception {
		mockMvc.perform(post("/api/mentor/withdrawal/bank-card/25")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(request)))
				.andExpect(status().isOk());
	}

	@Test
	public void withdrawWithBankCard_ThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException("") {
		})
				.when(withdrawalService)
				.withdrawWithBankCard(25L, request);

		mockMvc.perform(post("/api/mentor/withdrawal/bank-card/25")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void withdrawWithBankCard_ThrowMoneyRelatedException() throws Exception {
		doThrow(new MoneyRelatedException("") {
		})
				.when(withdrawalService)
				.withdrawWithBankCard(25L, request);

		mockMvc.perform(post("/api/mentor/withdrawal/bank-card/25")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void withdrawWithEWallet_Success() throws Exception {
		mockMvc.perform(post("/api/mentor/withdrawal/e-wallet/25")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(request)))
				.andExpect(status().isOk());
	}

	@Test
	public void withdrawWithEWallet_ThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException("") {
		})
				.when(withdrawalService)
				.withdrawWithEWallet(25L, request);

		mockMvc.perform(post("/api/mentor/withdrawal/e-wallet/25")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void withdrawWithEWallet_ThrowMoneyRelatedException() throws Exception {
		doThrow(new MoneyRelatedException("") {
		})
				.when(withdrawalService)
				.withdrawWithEWallet(25L, request);

		mockMvc.perform(post("/api/mentor/withdrawal/e-wallet/25")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(request)))
				.andExpect(status().isBadRequest());
	}
}