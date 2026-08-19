package org.acme.repositories;

import static org.acme.repositories.QuizRepository.QUIZ_CACHE_NAME;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.acme.entities.Answer;
import org.acme.entities.Question;
import org.acme.entities.Quiz;
import org.acme.enums.QuestionDifficulty;
import org.acme.enums.QuestionType;
import org.acme.exceptions.QuizNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class QuizRepositoryTest {

	@Inject
	QuizRepository quizRepository;

	@Inject
	@CacheName(QUIZ_CACHE_NAME)
	Cache quizCache;

	@BeforeEach
	void clearQuizCache() {
		quizCache.invalidateAll()
				.await()
				.indefinitely();
	}

	@Test
	void givenSavedQuiz_WhenFindingById_ThenReturnsQuiz() {
		Quiz quiz = quizFixture();

		quizRepository.save(quiz);

		assertEquals(quiz, quizRepository.findById(quiz.id()));
	}

	@Test
	void givenUnknownQuizId_WhenFindingById_ThenThrowsQuizNotFoundException() {
		UUID unknownQuizId = UUID.fromString("00000000-0000-0000-0000-000000000999");

		assertThrows(QuizNotFoundException.class, () -> quizRepository.findById(unknownQuizId));
	}

	@Test
	void givenExpiredQuiz_WhenFindingById_ThenThrowsQuizNotFoundException() {
		Quiz quiz = quizFixture();
		quizRepository.save(quiz);

		assertEquals(
				quiz,
				quizRepository.findById(quiz.id()));

		await()
				.atMost(Duration.ofSeconds(5))
				.pollInterval(Duration.ofMillis(50))
				.untilAsserted(() -> assertThrows(
						QuizNotFoundException.class,
						() -> quizRepository.findById(quiz.id())));
	}

	private Quiz quizFixture() {
		return new Quiz(
				UUID.fromString("00000000-0000-0000-0000-000000000001"),
				List.of(new Question(
						UUID.randomUUID(),
						QuestionType.MULTIPLE,
						QuestionDifficulty.EASY,
						"General Knowledge",
						"What is the answer?",
						List.of(new Answer(
								UUID.randomUUID(),
								"Wrong")),
						new Answer(
								UUID.randomUUID(),
								"Correct"))));
	}

}
