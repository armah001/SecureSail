package com.example.amalisecuresail.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entity class representing user account information.
 * This class is mapped to the "user_account" table in the database and includes fields
 * such as user ID, user reference, provider status, and ID verification status.
 */

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_account")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uuid;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;


    @PrePersist
    protected void OnAccountCreate(){
        this.uuid = UUID.randomUUID().toString().replace("-", "_");
    }
}
