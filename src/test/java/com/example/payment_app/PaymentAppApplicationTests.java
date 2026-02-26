package com.example.payment_app;

import com.example.payment_app.controller.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PaymentAppApplicationTests {

    @Autowired
    private AuthController authController;

	@Test
	void contextLoads() {
        assertThat (authController).isNotNull();
	}

}
