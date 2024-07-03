package devshaks.bank_microservices.roles;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for Role entities.
 * Extends JpaRepository to provide CRUD operations on Role entities.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Finds a role by its name.
     *
     * @param role The name of the role (an enum value of ERoles)
     * @return An Optional containing the found role, or empty if no role is found
     */
    Optional<Role> findByName(ERoles role);
}
