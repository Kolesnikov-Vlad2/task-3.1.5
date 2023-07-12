package com.itm.space.backendresources;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.spring.api.DBRider;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.net.URIBuilder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.keycloak.OAuth2Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.SerializationFeature;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;


import static com.itm.space.backendresources.utils.PropertyConstants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;


//@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ContextConfiguration(initializers = { KeycloakInitializer.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.Random.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@DBRider
@DBUnit(caseSensitiveTableNames = true, allowEmptyFields = true, cacheConnection = false)
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mvc;
    protected String authorization;
    @Value("${keycloak.port}")
    private int port;

    @BeforeAll
    public void getAuthorization() {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.put("token_name", Collections.singletonList("keycloak"));
        formData.put(OAuth2Constants.GRANT_TYPE, Collections.singletonList("password"));
        formData.put(OAuth2Constants.CLIENT_ID, Collections.singletonList("gateway"));
        formData.put(OAuth2Constants.CLIENT_SECRET, Collections.singletonList(KEYCLOAK_GATEWAY_CLIENT_SECRET));
        formData.put(OAuth2Constants.USERNAME, Collections.singletonList("dev@dev.ru"));
        formData.put(OAuth2Constants.PASSWORD, Collections.singletonList("passwd"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        URI authorizationURI = null;

        try {
            authorizationURI = new URIBuilder(KEYCLOAK_TOKEN_URL + port + KEYCLOAK_TOKEN_PATH).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        String result = restTemplate.postForObject(authorizationURI, request, String.class);

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        authorization = "Bearer " + jsonParser.parseMap(result)
                .get("access_token").toString();
    }

    private final ObjectWriter contentWriter = new ObjectMapper()
            .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
            .writer()
            .withDefaultPrettyPrinter();

    protected MockHttpServletRequestBuilder requestToJson(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder
                .contentType(APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder requestWithContent(MockHttpServletRequestBuilder requestBuilder,
                                                               Object content) throws JsonProcessingException {
        return requestToJson(requestBuilder).content(contentWriter.writeValueAsString(content));
    }
}
