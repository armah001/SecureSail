package com.example.amalisecuresail.repository;


import com.example.amalisecuresail.entity.User;
import com.example.amalisecuresail.entity.enums.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing User entities in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Retrieves a user by their email address.
     * @param email The email address of the user.
     * @return An Optional containing the user if found, otherwise empty.
     */
    Optional<User> findByEmail(String email);

    /**
     * Retrieves a user by their UUID.
     * @param uuid The UUID of the user.
     * @return The user with the specified UUID.
     */
    Optional<User> findByUuid(String uuid);

    /**
     * Retrieves a list of users whose verification status is true.
     * @return List of users with verified status set to true.
     */
    List<User> findByVerifiedTrue();

    /**
     * Retrieves a list of users whose verification status is false.
     * @return List of users with verified status set to false.
     */
    List<User> findByVerifiedFalse();

    /**
     * Retrieves a list of users whose account is locked.
     * @return List of users with locked accounts.
     */
    List<User> findByAccountLockedTrue();

    /**
     * Retrieves a list of users whose account is not locked.
     * @return List of users with unlocked accounts.
     */
    List<User> findByAccountLockedFalse();

    Optional<User> findByFullName(String superUser);
    List<User> findAllByActivityStatus(ActivityStatus activityStatus);

}

