package devshaks.bank_microservices.user;

import devshaks.bank_microservices.roles.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entity representing a user in the system.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {

    // Primary key for the User entity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;
    private String lastName;

    // Unique email for the user
    @Column(unique = true)
    private String email;

    private String password;
    private String phoneNumber;
    private Boolean emailVerified;
    public Boolean accountLocked;
    public Boolean enabled;

    // Timestamp of when the user was created, automatically set
    @CreationTimestamp
    @Column(insertable = false, updatable = false)
    private LocalDate createdAt;

    // User PIN, cannot be null
    @Column(nullable = false)
    private String userPin;

    // Many-to-many relationship with roles, eagerly fetched
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    /**
     * Gets the authorities granted to the user.
     *
     * @return A collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    /**
     * Gets the password of the user.
     *
     * @return The user's password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Gets the username of the user.
     *
     * @return The user's email as the username
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return True if the account is non-expired, false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user's account is locked.
     *
     * @return True if the account is non-locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    /**
     * Indicates whether the user's credentials have expired.
     *
     * @return True if the credentials are non-expired, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled.
     *
     * @return True if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets the first name of the user.
     *
     * @return The user's first name
     */
    @Override
    public String getName() {
        return firstName;
    }


    /**
     * Gets the full name of the user.
     *
     * @return The user's full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
