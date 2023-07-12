package com.itm.space.backendresources;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class KeycloakInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String KEYCLOAK_IMAGE_NAME = "quay.io/keycloak/keycloak:20.0";

    public static final KeycloakContainer container = new KeycloakContainer(KEYCLOAK_IMAGE_NAME)
            .withExposedPorts(8080)
            .withEnv("KEYCLOAK_ADMIN", "admin")
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
            .withRealmImportFile("keycloak/keycloak-realm.json");

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        container.start();

        TestPropertyValues.of(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:" + container.getHttpPort() + "/realms/ITM-Platform",
                "keycloak.port=" + container.getHttpPort()
        ).applyTo(applicationContext.getEnvironment());
    }
}



//    private static final String KEYCLOAK_IMAGE = "jboss/keycloak:18.0.2";
//    private static final int KEYCLOAK_PORT = 8080;
//    private static final String KEYCLOAK_REALM_FILE = "package.json";
//
//    @Override
//    public void initialize(ConfigurableApplicationContext applicationContext) {
//        GenericContainer<?> keycloakContainer = new GenericContainer<>(KEYCLOAK_IMAGE)
//                .withExposedPorts(KEYCLOAK_PORT)
//                .withEnv("KEYCLOAK_USER", "admin")
//                .withEnv("KEYCLOAK_PASSWORD", "admin")
//                .withCopyFileToContainer(MountableFile.forClasspathResource(KEYCLOAK_REALM_FILE), "/tmp/realm.json")
//                .withCommand("-b", "0.0.0.0")
//                .waitingFor(Wait.forHttp("/auth/realms/master").forStatusCode(200));
//
//        keycloakContainer.start();
//    }
//}
