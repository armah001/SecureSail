package com.example.amalisecuresail.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChangePasswordRequest {

    /**
     * The user's current password.
     */
    private String currentPassword;

    /**
     * The new password to be set.
     */
    private String newPassword;

    /**
     * The confirmation of the new password.
     */
    private String confirmationPassword;


}
