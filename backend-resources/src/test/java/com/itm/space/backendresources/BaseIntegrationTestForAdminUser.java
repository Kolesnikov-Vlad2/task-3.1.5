package com.itm.space.backendresources;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static com.itm.space.backendresources.utils.PropertyConstants.KEYCLOAK_GATEWAY_CLIENT_SECRET;
import static com.itm.space.backendresources.utils.PropertyConstants.KEYCLOAK_TOKEN_PATH;
import static com.itm.space.backendresources.utils.PropertyConstants.KEYCLOAK_TOKEN_URL;

public abstract class BaseIntegrationTestForAdminUser extends BaseIntegrationTest{

    @Autowired
    protected Keycloak keycloak;

    @Value("ITM-Platform")
    protected String realm;
    @Value("${keycloak.port}")
    private int port;


    @Override
    @BeforeAll
    public void getAuthorization() {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.put("token_name", Collections.singletonList("keycloak"));
        formData.put(OAuth2Constants.GRANT_TYPE, Collections.singletonList("password"));
        formData.put(OAuth2Constants.CLIENT_ID, Collections.singletonList("gateway"));
        formData.put(OAuth2Constants.CLIENT_SECRET, Collections.singletonList(KEYCLOAK_GATEWAY_CLIENT_SECRET));
        formData.put(OAuth2Constants.USERNAME, Collections.singletonList("admin@admin.ru"));
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
}