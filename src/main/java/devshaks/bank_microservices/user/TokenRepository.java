package devshaks.bank_microservices.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing Token entities.
 */
public interface TokenRepository extends JpaRepository<Token, Integer> {

    /**
     * Finds a Token entity by its token value.
     *
     * @param token The token value to search for
     * @return An Optional containing the found Token entity, or empty if no Token was found
     */
    Optional<Token> findByToken(String token);
}
