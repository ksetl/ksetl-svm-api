package org.ksetl.svm.system;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.ksetl.svm.ErrorResponse;
import org.ksetl.svm.SecurityRoles;
import org.ksetl.svm.TestData;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class SystemResourceTest {

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_SYSTEM_READ})
    public void getAll() {
        List<System> systems = List.of(given()
                .when()
                .get("/systems")
                .then()
                .statusCode(200)
                .extract()
                .as(System[].class));
        assertThat(systems).contains(
                new System(1, TestData.SYSTEM_1_NAME),
                new System(2, TestData.SYSTEM_2_NAME),
                new System(3, TestData.SYSTEM_3_NAME)
        );
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_SYSTEM_WRITE})
    public void getAllSecurityFail() {
        given()
                .when()
                .get("/systems")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_SYSTEM_READ, SecurityRoles.ROLE_SVM_SYSTEM_WRITE})
    public void postAndGetById() {
        System system = createSystem();
        System saved = given()
                .contentType(ContentType.JSON)
                .body(system)
                .post("/systems")
                .then()
                .statusCode(201)
                .extract().as(System.class);
        System got = given()
                .when()
                .get("/systems/{systemId}", saved.systemId())
                .then()
                .statusCode(200)
                .extract().as(System.class);
        assertThat(saved).isEqualTo(got);
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_SYSTEM_READ})
    public void getByIdNotFound() {
        given()
                .when()
                .get("/systems/{systemId}", 987654321)
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_SYSTEM_WRITE})
    public void postFailNoName() {
        System system = new System(null, null);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .body(system)
                .post("/systems")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);
        assertThat(errorResponse.getErrors())
                .hasSize(1)
                .contains(new ErrorResponse.ErrorMessage("post.system.name", "System name is required"));
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_SYSTEM_WRITE, SecurityRoles.ROLE_SVM_SYSTEM_READ})
    public void put() {
        System system = createSystem();
        System saved = given()
                .contentType(ContentType.JSON)
                .body(system)
                .post("/systems")
                .then()
                .statusCode(201)
                .extract().as(System.class);
        System updated = new System(saved.systemId(), "updated");
        given()
                .contentType(ContentType.JSON)
                .body(updated)
                .put("/systems/{systemId}", saved.systemId())
                .then()
                .statusCode(204);
        System got = given()
                .when()
                .get("/systems/{systemId}", saved.systemId())
                .then()
                .statusCode(200)
                .extract().as(System.class);
        assertThat(got.name()).isEqualTo("updated");
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_ADMIN})
    public void putForAdmin() {
        put();
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_SYSTEM_WRITE})
    public void putFailNoName() {
        System system = createSystem();
        System saved = given()
                .contentType(ContentType.JSON)
                .body(system)
                .post("/systems")
                .then()
                .statusCode(201)
                .extract().as(System.class);
        System updated = new System(saved.systemId(), null);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .body(updated)
                .put("/systems/{systemId}", saved.systemId())
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);
        assertThat(errorResponse.getErrors())
                .hasSize(1)
                .contains(new ErrorResponse.ErrorMessage("put.system.name", "System name is required"));
    }

    private System createSystem() {
        System system = new System(null, RandomStringUtils.randomAlphabetic(10));
        return system;
    }
}
