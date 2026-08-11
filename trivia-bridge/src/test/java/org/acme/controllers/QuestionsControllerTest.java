package org.acme.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.acme.clients.OpenTriviaClient;
import org.acme.dtos.QuestionsRequestDTO;
import org.acme.entities.Answer;
import org.acme.entities.Question;
import org.acme.entities.Quiz;
import org.acme.enums.QuestionDifficulty;
import org.acme.enums.QuestionType;
import org.acme.exceptions.NoQuestionsAvailableException;
import org.acme.exceptions.QuestionProviderException;
import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(QuestionsController.class)
class QuestionsControllerTest {

	@InjectMock
	OpenTriviaClient openTriviaClient;

	@Test
	void givenValidQuestionRequest_WhenGettingQuestions_ThenReturnsQuizResponse() {
		QuestionsRequestDTO request = validQuestionsRequestDTOFixture();
		Quiz quiz = validQuizEntityFixture();

		when(openTriviaClient.get(request)).thenReturn(quiz);

		given()
			.queryParam("amount", 1)
			.queryParam("category", 9)
			.queryParam("difficulty", "EASY")
			.queryParam("type","MULTIPLE")
		.when()
			.get()
		.then()
			.statusCode(200)
			.body("id", notNullValue())
			.body("questions", hasSize(1))
			.body("questions[0].id", notNullValue())
			.body("questions[0].category", equalTo("General Knowledge"))
			.body("questions[0].difficulty", equalTo("easy"))
			.body("questions[0].type", equalTo("multiple"))
			.body("questions[0].question", equalTo("What is the answer?"))
			.body("questions[0].options.text",
					containsInAnyOrder("Correct", "Wrong 1", "Wrong 2"));
	}

	@Test
	void givenInvalidQuestionRequest_WhenGettingQuestions_ThenReturnsBadRequest() {
		given()
			.queryParam("amount", 0)
			.queryParam("category", 9)
			.queryParam("difficulty", "EASY")
			.queryParam("type","MULTIPLE")
		.when()
			.get()
		.then()
			.statusCode(400);
	}

	@Test
	void givenNoQuestionsAvailable_WhenGettingQuestions_ThenReturnsNotFound() {
		QuestionsRequestDTO request = validQuestionsRequestDTOFixture();

		when(openTriviaClient.get(request)).thenThrow(new NoQuestionsAvailableException());

		given()
			.queryParam("amount", 1)
			.queryParam("category", 9)
			.queryParam("difficulty", "EASY")
			.queryParam("type","MULTIPLE")
		.when()
			.get()
		.then()
			.statusCode(404)
			.body("code", equalTo("NO_QUESTIONS_AVAILABLE"))
			.body("message", equalTo("No questions available for the requested parameters."));
	}

	@Test
	void givenQuestionProviderFailure_WhenGettingQuestions_ThenReturnsBadGateway() {
		QuestionsRequestDTO request = validQuestionsRequestDTOFixture();

		when(openTriviaClient.get(request)).thenThrow(new QuestionProviderException());

		given()
			.queryParam("amount", 1)
			.queryParam("category", 9)
			.queryParam("difficulty", "EASY")
			.queryParam("type","MULTIPLE")
		.when()
			.get()
		.then()
			.statusCode(502)
			.body("code", equalTo("QUESTION_PROVIDER_ERROR"))
			.body("message", equalTo("Unable to retrieve questions"));
	}

	private QuestionsRequestDTO validQuestionsRequestDTOFixture() {
		return new QuestionsRequestDTO(
				1,
				9,
				QuestionDifficulty.EASY,
				QuestionType.MULTIPLE);
	}

	private Quiz validQuizEntityFixture() {
		return new Quiz(
				UUID.randomUUID(),
				List.of(new Question(
						UUID.randomUUID(),
						QuestionType.MULTIPLE,
						QuestionDifficulty.EASY,
						"General Knowledge",
						"What is the answer?",
						List.of(
								new Answer(
										UUID.randomUUID(),
										"Wrong 1"),
								new Answer(
										UUID.randomUUID(),
										"Wrong 2")),
								new Answer(
										UUID.randomUUID(),
										"Correct"))),
				Instant.now().plusSeconds(3600));
	}
}
