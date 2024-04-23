package com.example.amalisecuresail.entity;

import com.example.amalisecuresail.entity.enums.ActivityStatus;
import com.example.amalisecuresail.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Entity class representing a user in the application.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
@ToString(exclude = "tokens")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Integer id;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PasswordResetToken> tokens;

    @Column(unique = true)
    private String uuid = UUID.randomUUID().toString();

    @NotBlank(message = "Full name is mandatory")
    @Size(min = 5, message = "Enter both first name and last name")
    private String fullName;

    @Email
    @NotBlank(message = "Email is mandatory")
    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column( nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private boolean accountLocked;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime createdAt;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime lastLoginTime;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAccount userAccount;

    @Enumerated(EnumType.STRING)
    private ActivityStatus activityStatus;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return verified;
    }

    @JsonIgnore
    public String getName() {
        return fullName;
    }

    @PrePersist
    protected void onUserCreate (){
        this.uuid = UUID.randomUUID().toString().replace("-", "_");
    }

}

