package devshaks.bank_microservices;

import devshaks.bank_microservices.roles.ERoles;
import devshaks.bank_microservices.roles.Role;
import devshaks.bank_microservices.roles.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.Async;

@SpringBootApplication
@EnableJpaAuditing
@Async
public class BankMicroservicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankMicroservicesApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			for (ERoles roleName : ERoles.values()) {
				if (roleRepository.findByName(roleName).isEmpty()) {
					roleRepository.save(Role.builder().name(roleName).build());
				}
 			}
		};
	}

}
