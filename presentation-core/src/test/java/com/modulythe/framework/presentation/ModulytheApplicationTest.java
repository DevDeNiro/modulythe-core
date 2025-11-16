package com.modulythe.framework.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.cloud.config.enabled=false")
class ModulytheApplicationTest {
    @Test
    void contextLoads() {
    }
}
