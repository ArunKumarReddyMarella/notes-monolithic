package com.enotes.monolithic.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class  EmailRequest {
    private String to;
    private String title;
    private String subject;
    private String message;
}
