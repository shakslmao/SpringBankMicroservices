package devshaks.bank_microservices.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user
     * @return An Optional containing the found user, or empty if no user is found
     */
    Optional<User> findByEmail(String email);
}
