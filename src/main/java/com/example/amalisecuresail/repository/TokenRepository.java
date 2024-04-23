package com.example.amalisecuresail.repository;


import com.example.amalisecuresail.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * TokenRepository is a Spring Data JPA repository for managing Token entities in the database.
 *
 * @Repository Indicates that this interface is a Spring Data repository, responsible for database operations on Token entities.
 */
@Repository
public interface TokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    /**
     * Retrieves a list of valid tokens associated with a user based on user ID.
     *
     * @param id The ID of the user.
     * @return A list of Token entities that are valid, not used, and have not expired for the specified user.
     */
    @Query(value = "select t from PasswordResetToken t inner join User u " +
            "on t.user.id = u.id " +
            "where u.id = :id and t.used = false and t.expiryDate > CURRENT_TIMESTAMP")
    List<PasswordResetToken> findAllValidTokenByUser(Integer id);

    /**
     * Retrieves an Optional<Token> based on the token value.
     *
     * @param token The token value.
     * @return An Optional<Token> containing the Token entity if found, or an empty Optional if not found.
     */
    Optional<PasswordResetToken> findByToken(String token);

}
