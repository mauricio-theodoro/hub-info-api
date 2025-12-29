package br.com.hubinfo.hub_info_api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Context-load depende do banco; será reativado quando configurarmos Testcontainers")
@SpringBootTest
class HubInfoApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
