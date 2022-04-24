package com.backend.apiserver.bean.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EWalletRequest {
    private String holderName;
    @JsonProperty("eWalletName")
    private String eWalletName;
    private String phone;
}
