package com.example.amalisecuresail.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token")
public class PasswordResetToken {

    /**
     * The unique identifier for the token.
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * The randomly generated token value using UUID.
     */
    private String token = UUID.randomUUID().toString().replace("-", "");

    /**
     * The user associated with this token.
     *
     * @ManyToOne Indicates a many-to-one relationship with the User entity.
     * @JoinColumn(name = "user_id") Specifies the name of the foreign key column in the database.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    /**
     * The expiration date and time of the token.
     */
    private LocalDateTime expiryDate;

    /**
     * A flag indicating whether the token has been used.
     */
    private boolean used;

    /**
     * The date and time when the token was created.
     */
    private LocalDateTime createdAt;

    /**
     * The date and time when the token was last updated.
     */
    private LocalDateTime updatedAt;

}

