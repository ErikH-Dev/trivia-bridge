package org.acme.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;
import java.util.UUID;

import org.acme.dtos.AnswerCheckRequestDTO;
import org.acme.dtos.QuizCheckRequestDTO;
import org.acme.entities.Answer;
import org.acme.entities.Question;
import org.acme.entities.Quiz;
import org.acme.enums.QuestionDifficulty;
import org.acme.enums.QuestionType;
import org.acme.repositories.QuizRepository;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

@QuarkusTest
@TestHTTPEndpoint(CheckAnswersController.class)
class CheckAnswersControllerTest {

	private static final UUID QUIZ_ID = UUID.fromString("00000000-0000-0000-0000-000000000300");
	private static final UUID QUESTION_ID = UUID.fromString("00000000-0000-0000-0000-000000000301");
	private static final UUID CORRECT_ANSWER_ID = UUID.fromString("00000000-0000-0000-0000-000000000302");
	private static final UUID WRONG_ANSWER_ID = UUID.fromString("00000000-0000-0000-0000-000000000303");

	@Inject
	QuizRepository quizRepository;

	@Test
	void givenCorrectAnswer_WhenCheckingAnswers_ThenReturnsQuizResult() {
		quizRepository.save(validQuizEntityFixture());
		QuizCheckRequestDTO request = new QuizCheckRequestDTO(
				QUIZ_ID,
				List.of(new AnswerCheckRequestDTO(QUESTION_ID, CORRECT_ANSWER_ID)));

		given()
				.contentType(ContentType.JSON)
				.body(request)
				.when()
				.post()
				.then()
				.statusCode(200)
				.body("quizId", equalTo(QUIZ_ID.toString()))
				.body("correctAnswerCount", equalTo(1))
				.body("totalQuestions", equalTo(1))
				.body("questionResults", hasSize(1))
				.body("questionResults[0].questionId", equalTo(QUESTION_ID.toString()))
				.body("questionResults[0].selectedAnswerId", equalTo(CORRECT_ANSWER_ID.toString()))
				.body("questionResults[0].correctAnswerId", equalTo(CORRECT_ANSWER_ID.toString()))
				.body("questionResults[0].correct", equalTo(true));
	}

	@Test
	void givenEmptyAnswers_WhenCheckingAnswers_ThenReturnsBadRequest() {
		QuizCheckRequestDTO request = new QuizCheckRequestDTO(QUIZ_ID, List.of());

		given()
				.contentType(ContentType.JSON)
				.body(request)
				.when()
				.post()
				.then()
				.statusCode(400);
	}

	@Test
	void givenUnknownQuiz_WhenCheckingAnswers_ThenReturnsNotFound() {
		UUID unknownQuizId = UUID.fromString("00000000-0000-0000-0000-000000000399");
		QuizCheckRequestDTO request = new QuizCheckRequestDTO(
				unknownQuizId,
				List.of(new AnswerCheckRequestDTO(QUESTION_ID, CORRECT_ANSWER_ID)));

		given()
				.contentType(ContentType.JSON)
				.body(request)
				.when()
				.post()
				.then()
				.statusCode(404)
				.body("code", equalTo("QUIZ_NOT_FOUND"));
	}

	@Test
	void givenUnknownQuestion_WhenCheckingAnswers_ThenReturnsNotFound() {
		quizRepository.save(validQuizEntityFixture());
		UUID unknownQuestionId = UUID.fromString("00000000-0000-0000-0000-000000000398");
		QuizCheckRequestDTO request = new QuizCheckRequestDTO(
				QUIZ_ID,
				List.of(new AnswerCheckRequestDTO(unknownQuestionId, CORRECT_ANSWER_ID)));

		given()
				.contentType(ContentType.JSON)
				.body(request)
				.when()
				.post()
				.then()
				.statusCode(404)
				.body("code", equalTo("QUESTION_NOT_FOUND"));
	}

	@Test
	void givenUnknownAnswer_WhenCheckingAnswers_ThenReturnsNotFound() {
		quizRepository.save(validQuizEntityFixture());
		UUID unknownAnswerId = UUID.fromString("00000000-0000-0000-0000-000000000397");
		QuizCheckRequestDTO request = new QuizCheckRequestDTO(
				QUIZ_ID,
				List.of(new AnswerCheckRequestDTO(QUESTION_ID, unknownAnswerId)));

		given()
				.contentType(ContentType.JSON)
				.body(request)
				.when()
				.post()
				.then()
				.statusCode(404)
				.body("code", equalTo("ANSWER_NOT_FOUND"));
	}

	@Test
	void givenNullRequestBody_WhenCheckingAnswers_ThenReturnsValidationError() {
		given()
				.contentType(ContentType.JSON)
				.body("null")
				.when()
				.post()
				.then()
				.statusCode(400)
				.body("code", equalTo("VALIDATION_ERROR"))
				.body("message", equalTo("Request body is required"));
	}

	private Quiz validQuizEntityFixture() {
		return new Quiz(
				QUIZ_ID,
				List.of(new Question(
						QUESTION_ID,
						QuestionType.MULTIPLE,
						QuestionDifficulty.EASY,
						"General Knowledge",
						"What is the answer?",
						List.of(new Answer(WRONG_ANSWER_ID, "Wrong")),
						new Answer(CORRECT_ANSWER_ID, "Correct"))));
	}
}
