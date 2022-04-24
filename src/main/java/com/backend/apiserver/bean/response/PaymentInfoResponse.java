package com.backend.apiserver.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentInfoResponse {
	private boolean isEWallet;
	private String holderName;
	@JsonProperty("eWalletName")
	private String eWalletName;
	private String phone;
	private String accountNumber;
	private String bank;
	private String branch;
}
