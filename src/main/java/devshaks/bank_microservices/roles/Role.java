package devshaks.bank_microservices.roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import devshaks.bank_microservices.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a role in the system.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Role {

    // Primary key for the Role entity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Enum representing the name of the role, unique and non-nullable
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private ERoles name;

    // Many-to-many relationship with User, roles are mapped by the "roles" field in User
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<User> users;

    // Timestamp for when the role was created, automatically set and non-updatable
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    // Timestamp for when the role was last modified, automatically updated
    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
}
