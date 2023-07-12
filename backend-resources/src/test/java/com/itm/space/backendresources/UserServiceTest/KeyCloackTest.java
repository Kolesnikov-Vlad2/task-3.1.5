package com.itm.space.backendresources.UserServiceTest;

import com.github.database.rider.core.api.dataset.DataSet;
import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

//@WithMockUser(username = "admin", password = "admin", authorities = "ROLE_MODERATOR")
public class KeyCloackTest extends BaseIntegrationTest {
//    @Autowired
//    private UserService userService;



    @Test
    @DataSet(value = {"datasets/groups.yml",
            "datasets/roles.yml"},
            cleanAfter = true, cleanBefore = true)
    public void createUserTest() throws Exception {
        UserRequest userRequest = new UserRequest("user", "asdfgh@mail.com", "12345", "John", "Doe");
//        userService.createUser(userRequest);

        Response response = Response.status(Response.Status.CREATED).location(new URI("user_id")).build();

    }

}