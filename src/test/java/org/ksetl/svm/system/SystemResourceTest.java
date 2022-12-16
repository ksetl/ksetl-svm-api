package org.ksetl.svm.system;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class SystemResourceTest {

    @Test
    public void getAll() {
        given()
                .when()
                .get("/systems")
                .then()
                .statusCode(200);
    }

    @Test
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
    public void getByIdNotFound() {
        given()
                .when()
                .get("/systems/{systemId}", 987654321)
                .then()
                .statusCode(404);
    }

    @Test
    public void postFailNoName() {
        System system = new System(null, null);
        given()
                .contentType(ContentType.JSON)
                .body(system)
                .post("/systems")
                .then()
                .statusCode(400);
    }

    @Test
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
                .body(saved)
                .put("/systems/{systemId}", saved.systemId())
                .then()
                .statusCode(204);
    }

    @Test
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
        given()
                .contentType(ContentType.JSON)
                .body(updated)
                .put("/systems/{systemId}", saved.systemId())
                .then()
                .statusCode(400);
    }

    private System createSystem() {
        System system = new System(null, RandomStringUtils.randomAlphabetic(10));
        return system;
    }
}
