package devshaks.bank_microservices.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Street is required")
    @NotBlank(message = "Street is required")
    private String street;

    @NotEmpty(message = "City is required")
    @NotBlank(message = "City is required")
    private String city;

    @NotEmpty(message = "County is required")
    @NotBlank(message = "County is required")
    private String county;

    @NotEmpty(message = "Postal Code is required")
    @NotBlank(message = "Postal Code is required")
    @Size(min = 5, max = 10, message = "Postal Code must be between 5 and 10 characters")
    private String postalCode;

    @NotEmpty(message = "Country is required")
    @NotBlank(message = "Country is required")
    private String country;
}