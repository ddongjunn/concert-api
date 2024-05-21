package com.api.concert;

import config.TestContainerSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ConcertApplicationTests extends TestContainerSupport {

	@Test
	void contextLoads() {
	}

}
