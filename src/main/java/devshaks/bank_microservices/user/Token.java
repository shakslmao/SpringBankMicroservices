package devshaks.bank_microservices.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Integer id;

    private String token;
    private LocalDateTime createdTokenAt;
    private LocalDateTime expiredTokenAt;
    private LocalDateTime validatedTokenAt;

    @ManyToOne
    @JoinColumn(name="userId", nullable = false)
    private User user;

}
