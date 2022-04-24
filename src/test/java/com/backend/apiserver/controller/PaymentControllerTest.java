package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.BankCardRequest;
import com.backend.apiserver.bean.request.EWalletRequest;
import com.backend.apiserver.bean.response.BankCardResponse;
import com.backend.apiserver.bean.response.EWalletResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.BankCardService;
import com.backend.apiserver.service.EWalletService;
import com.backend.apiserver.controller.PaymentController;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class PaymentControllerTest {

	@InjectMocks
	PaymentController paymentController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private BankCardService bankCardService;

	@Mock
	private EWalletService eWalletService;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(paymentController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void getMentorBankCards() throws Exception {
		when(bankCardService.getBankCards()).thenReturn(generateBankCards());
		mockMvc.perform(get("/api/mentor/bank-cards"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(2)))
				.andExpect(jsonPath("$.data[0].id", is(5)))
				.andExpect(jsonPath("$.data[0].accountNumber", is("8103205071284")))
				.andExpect(jsonPath("$.data[0].bank", is("Agribank")))
				.andExpect(jsonPath("$.data[0].branch", is("HÀ NỘI")))
				.andExpect(jsonPath("$.data[0].holderName", is("Đặng Đình Quyền")))
				.andExpect(jsonPath("$.data[1].id", is(6)))
				.andExpect(jsonPath("$.data[1].accountNumber", is("02928683402")))
				.andExpect(jsonPath("$.data[1].bank", is("TPBank")))
				.andExpect(jsonPath("$.data[1].branch", is("HÀ NỘI")))
				.andExpect(jsonPath("$.data[1].holderName", is("Đặng Đình Quyền")));
	}

	private WrapperResponse generateBankCards() {
		BankCardResponse bankCardResponse1 = new BankCardResponse();
		bankCardResponse1.setId(5L);
		bankCardResponse1.setAccountNumber("8103205071284");
		bankCardResponse1.setBank("Agribank");
		bankCardResponse1.setBranch("HÀ NỘI");
		bankCardResponse1.setHolderName("Đặng Đình Quyền");

		BankCardResponse bankCardResponse2 = new BankCardResponse();
		bankCardResponse2.setId(6L);
		bankCardResponse2.setAccountNumber("02928683402");
		bankCardResponse2.setBank("TPBank");
		bankCardResponse2.setBranch("HÀ NỘI");
		bankCardResponse2.setHolderName("Đặng Đình Quyền");
		return new WrapperResponse(Arrays.asList(bankCardResponse1, bankCardResponse2));
	}

	private BankCardResponse generateBankCard() {
		BankCardResponse bankCardResponse1 = new BankCardResponse();
		bankCardResponse1.setId(5L);
		bankCardResponse1.setAccountNumber("8103205071284");
		bankCardResponse1.setBank("Agribank");
		bankCardResponse1.setBranch("HÀ NỘI");
		bankCardResponse1.setHolderName("Đặng Đình Quyền");
		return bankCardResponse1;
	}

	@Test
	public void getMentorEWallets() throws Exception {
		when(eWalletService.getEWallets()).thenReturn(generateEWallets());
		mockMvc.perform(get("/api/mentor/e-wallets"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(2)))
				.andExpect(jsonPath("$.data[0].id", is(5)))
				.andExpect(jsonPath("$.data[0].phone", is("0969563145")))
				.andExpect(jsonPath("$.data[0].eWalletName", is("MOMO")))
				.andExpect(jsonPath("$.data[0].holderName", is("Đặng Đình Quyền")))
				.andExpect(jsonPath("$.data[1].id", is(6)))
				.andExpect(jsonPath("$.data[1].phone", is("0969563145")))
				.andExpect(jsonPath("$.data[1].eWalletName", is("AIRPAY")))
				.andExpect(jsonPath("$.data[1].holderName", is("Đặng Đình Quyền")));
	}

	private WrapperResponse generateEWallets() {
		EWalletResponse eWalletResponse1 = new EWalletResponse();
		eWalletResponse1.setId(5L);
		eWalletResponse1.setPhone("0969563145");
		eWalletResponse1.setEWalletName("MOMO");
		eWalletResponse1.setHolderName("Đặng Đình Quyền");

		EWalletResponse eWalletResponse2 = new EWalletResponse();
		eWalletResponse2.setId(6L);
		eWalletResponse2.setPhone("0969563145");
		eWalletResponse2.setEWalletName("AIRPAY");
		eWalletResponse2.setHolderName("Đặng Đình Quyền");
		return new WrapperResponse(Arrays.asList(eWalletResponse1, eWalletResponse2));
	}

	private EWalletResponse generateEWallet() {
		EWalletResponse eWalletResponse1 = new EWalletResponse();
		eWalletResponse1.setId(5L);
		eWalletResponse1.setPhone("0969563145");
		eWalletResponse1.setEWalletName("MOMO");
		eWalletResponse1.setHolderName("Đặng Đình Quyền");
		return eWalletResponse1;
	}

	@Test
	public void createBankCard() throws Exception {
		when(bankCardService.createBankCard(any())).thenReturn(generateBankCard());
		mockMvc.perform(post("/api/mentor/bank-card")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateBankCardRequest())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(5)))
				.andExpect(jsonPath("$.accountNumber", is("8103205071284")))
				.andExpect(jsonPath("$.bank", is("Agribank")))
				.andExpect(jsonPath("$.branch", is("HÀ NỘI")))
				.andExpect(jsonPath("$.holderName", is("Đặng Đình Quyền")));
	}

	private BankCardRequest generateBankCardRequest() {
		BankCardRequest bankCardRequest = new BankCardRequest();
		bankCardRequest.setAccountNumber("8103205071284");
		bankCardRequest.setBank("Agribank");
		bankCardRequest.setBranch("HÀ NỘI");
		bankCardRequest.setHolderName("Đặng Đình Quyền");
		return bankCardRequest;
	}

	private EWalletRequest generateEWalletRequest() {
		EWalletRequest eWalletRequest = new EWalletRequest();
		eWalletRequest.setPhone("0969563145");
		eWalletRequest.setEWalletName("MOMO");
		eWalletRequest.setHolderName("Đặng Đình Quyền");
		return eWalletRequest;
	}

	@Test
	public void createEWallet() throws Exception {
		when(eWalletService.createEWallet(any())).thenReturn(generateEWallet());
		mockMvc.perform(post("/api/mentor/e-wallet")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateEWalletRequest())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(5)))
				.andExpect(jsonPath("$.phone", is("0969563145")))
				.andExpect(jsonPath("$.eWalletName", is("MOMO")))
				.andExpect(jsonPath("$.holderName", is("Đặng Đình Quyền")));
	}

	@Test
	public void updateBankCard() throws Exception {
		mockMvc.perform(put("/api/mentor/bank-card/5")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateBankCardRequest())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("106")));
	}

	@Test
	public void updateBankCard_ErrorThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException()).when(bankCardService).updateBankCard(any(), any());
		mockMvc.perform(put("/api/mentor/bank-card/5")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateBankCardRequest())))
				.andExpect(jsonPath("$.code", is("103")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void updateEWallet() throws Exception {
		mockMvc.perform(put("/api/mentor/e-wallet/5")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateEWalletRequest())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("106")));
	}

	@Test
	public void updateEWallet_ErrorThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException()).when(eWalletService).updateEWallet(any(), any());
		mockMvc.perform(put("/api/mentor/e-wallet/5")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateEWalletRequest())))
				.andExpect(jsonPath("$.code", is("103")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void deleteBankCard() throws Exception {
		mockMvc.perform(delete("/api/mentor/bank-card/5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("107")));
	}

	@Test
	public void deleteBankCard_ErrorThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException()).when(bankCardService).deleteBankCard(any());
		mockMvc.perform(delete("/api/mentor/bank-card/5"))
				.andExpect(jsonPath("$.code", is("103")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void deleteEWallet() throws Exception {
		mockMvc.perform(delete("/api/mentor/e-wallet/5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("107")));
	}

	@Test
	public void deleteEWallet_ErrorThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException()).when(eWalletService).deleteEWallet(any());
		mockMvc.perform(delete("/api/mentor/e-wallet/5"))
				.andExpect(jsonPath("$.code", is("103")))
				.andExpect(status().isBadRequest());
	}
}