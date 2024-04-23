package com.example.amalisecuresail.repository;

import com.example.amalisecuresail.entity.User;
import com.example.amalisecuresail.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface to query {@link com.example.amalisecuresail.entity.UserAccount} database records
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    Optional<UserAccount> findByUuid(String uuid);

    Optional<UserAccount> findByUser(User user);

    Optional<UserAccount> findByUserUuid(String userUuid);

    Optional<UserAccount> findUserAccountByUser_FullName(String fullName);

    Optional<UserAccount> findUserAccountByUser_Uuid(String reportedUuid);
}