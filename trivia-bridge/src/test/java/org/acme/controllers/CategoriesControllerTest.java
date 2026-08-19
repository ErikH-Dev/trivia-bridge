package org.acme.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

import java.util.List;

import org.acme.dtos.response.CategoryResponseDTO;
import org.acme.exceptions.CategoryProviderException;
import org.acme.interfaces.IQuestionProvider;
import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(CategoriesController.class)
class CategoriesControllerTest {

    @InjectMock
    IQuestionProvider questionProvider;

    @Test
    void givenCategories_WhenGettingCategories_ThenReturnsCategoryResponse() {
        List<CategoryResponseDTO> categories = List.of(
                new CategoryResponseDTO(9, "General Knowledge"),
                new CategoryResponseDTO(10, "Entertainment: Books"));

        when(questionProvider.getCategories()).thenReturn(categories);

        given()
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("trivia_categories", hasSize(2))
                .body("trivia_categories[0].id", equalTo(9))
                .body("trivia_categories[0].name", equalTo("General Knowledge"))
                .body("trivia_categories[1].id", equalTo(10))
                .body("trivia_categories[1].name", equalTo("Entertainment: Books"));
    }

    @Test
    void givenProviderFailure_WhenGettingCategories_ThenReturnsBadGateway() {
        when(questionProvider.getCategories())
                .thenThrow(new CategoryProviderException("Unable to retrieve categories"));

        given()
                .when()
                .get()
                .then()
                .statusCode(502)
                .body("code", equalTo("CATEGORY_PROVIDER_ERROR"))
                .body("message", equalTo("Unable to retrieve categories"));
    }
}
