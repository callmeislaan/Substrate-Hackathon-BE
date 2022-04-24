package com.backend.apiserver.bean.request;

import lombok.Data;

@Data
public class NotificationRequest {
    private String type;
    private long requestId;
    private String title;
    private String content;
    private long created;
}
