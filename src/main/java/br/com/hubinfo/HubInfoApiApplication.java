package br.com.hubinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HUB Info - API principal.
 *
 * Responsabilidade:
 * - Subir o contexto Spring e disponibilizar os endpoints REST do sistema.
 *
 * Observação de arquitetura:
 * - A API seguirá Clean Architecture por feature, separando camadas (domain/usecase/adapters).
 */
@SpringBootApplication
public class HubInfoApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(HubInfoApiApplication.class, args);
	}
}