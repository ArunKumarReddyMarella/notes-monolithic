package com.enotes.monolithic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChngRequest {

    private String currentPassword;
    private String newPassword;
}
