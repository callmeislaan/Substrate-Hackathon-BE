package com.backend.apiserver.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EWalletResponse {
	private Long id;
	private String holderName;
	@JsonProperty("eWalletName")
	private String eWalletName;
	private String phone;
}
