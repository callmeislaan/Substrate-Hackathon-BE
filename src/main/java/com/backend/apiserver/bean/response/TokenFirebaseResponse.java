package com.backend.apiserver.bean.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenFirebaseResponse {
    private String firebaseToken;
}
