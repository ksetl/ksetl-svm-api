package org.ksetl.svm.mapping;

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
public class MappingResourceTest {

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_MAPPING_READ})
    public void getAll() {
        List<MappingView> mappingViews = List.of(given()
                .when()
                .get("/mappings")
                .then()
                .statusCode(200)
                .extract()
                .as(MappingView[].class));
        assertThat(mappingViews).contains(
                new MappingView(1, 1, "Field1", "Value1",
                        2, "Value2", ValueType.STRING)
        );
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_MAPPING_WRITE})
    public void getAllSecurityFail() {
        given()
                .when()
                .get("/mappings")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_MAPPING_WRITE, SecurityRoles.ROLE_SVM_MAPPING_READ})
    public void postAndGetById() {
        MappingView mappingView = createMappingView();
        MappingView saved = given()
                .contentType(ContentType.JSON)
                .body(mappingView)
                .post("/mappings")
                .then()
                .statusCode(201)
                .extract().as(MappingView.class);
        MappingView got = given()
                .when()
                .get("/mappings/{mappingId}", saved.mappingId())
                .then()
                .statusCode(200)
                .extract().as(MappingView.class);
        assertThat(saved).isEqualTo(got);
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_MAPPING_READ})
    public void getByIdNotFound() {
        given()
                .when()
                .get("/mappings/{mappingId}", 987654321)
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_MAPPING_WRITE})
    public void postFailNoSourceSystem() {
        MappingView mappingView = new MappingView(null,
                null, "Field1", "Value1",
               2, "Value2", ValueType.STRING);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .body(mappingView)
                .post("/mappings")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);
        assertThat(errorResponse.getErrors())
                .hasSize(1)
                .contains(new ErrorResponse.ErrorMessage("post.mappingView.sourceSystemId", "Source System Id is required"));
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_MAPPING_WRITE, SecurityRoles.ROLE_SVM_MAPPING_READ})
    public void put() {
        MappingView mappingView = createMappingView();
        MappingView saved = given()
                .contentType(ContentType.JSON)
                .body(mappingView)
                .post("/mappings")
                .then()
                .statusCode(201)
                .extract().as(MappingView.class);
        MappingView updated = new MappingView(saved.mappingId(), saved.sourceSystemId(), saved.sourceFieldName(), saved.sourceValue(),
                saved.targetSystemId(), "updated", saved.targetValueType());
        given()
                .contentType(ContentType.JSON)
                .body(updated)
                .put("/mappings/{mappingId}", updated.mappingId())
                .then()
                .statusCode(204);
        MappingView got = given()
                .when()
                .get("/mappings/{mappingId}", updated.mappingId())
                .then()
                .statusCode(200)
                .extract().as(MappingView.class);
        assertThat(got.targetValue()).isEqualTo("updated");
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_ADMIN})
    public void putForAdmin() {
        put();
    }

    @Test
    @TestSecurity(user = TestData.TEST_USER, roles = {SecurityRoles.ROLE_SVM_MAPPING_WRITE})
    public void putFailNoSourceValue() {
        MappingView mappingView = createMappingView();
        MappingView saved = given()
                .contentType(ContentType.JSON)
                .body(mappingView)
                .post("/mappings")
                .then()
                .statusCode(201)
                .extract().as(MappingView.class);
        MappingView updated = new MappingView(saved.mappingId(), saved.sourceSystemId(), saved.sourceFieldName(), "",
                saved.targetSystemId(), saved.targetValue(), saved.targetValueType());
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .body(updated)
                .put("/mappings/{mappingId}", saved.mappingId())
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);
        assertThat(errorResponse.getErrors())
                .hasSize(1)
                .contains(new ErrorResponse.ErrorMessage("put.mappingView.sourceValue", "Source Value is required"));
    }

    private MappingView createMappingView() {
        MappingView mapping = new MappingView(null, 1,
                RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), 2,
                RandomStringUtils.randomAlphabetic(10), ValueType.STRING);
        return mapping;
    }
}
