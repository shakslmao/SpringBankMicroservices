package devshaks.bank_microservices.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity class representing a Token.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

    /**
     * Unique identifier for the Token.
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * The token string.
     */
    private String token;

    /**
     * The date and time when the token was created.
     */
    private LocalDateTime createdTokenAt;

    /**
     * The date and time when the token expires.
     */
    private LocalDateTime expiredTokenAt;

    /**
     * The date and time when the token was validated.
     */
    private LocalDateTime validatedTokenAt;

    /**
     * The user associated with the token.
     * A many-to-one relationship with the User entity.
     */
    @ManyToOne
    @JoinColumn(name="userId", nullable = false)
    private User user;

}
