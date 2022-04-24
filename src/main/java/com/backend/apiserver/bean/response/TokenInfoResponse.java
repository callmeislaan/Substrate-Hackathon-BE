package com.backend.apiserver.bean.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenInfoResponse {
    private String token;
    private String username;
    private String role;
}
