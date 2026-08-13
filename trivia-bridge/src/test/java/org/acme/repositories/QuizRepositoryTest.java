package org.acme.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.acme.entities.Answer;
import org.acme.entities.Question;
import org.acme.entities.Quiz;
import org.acme.enums.QuestionDifficulty;
import org.acme.enums.QuestionType;
import org.acme.exceptions.QuizNotFoundException;
import org.junit.jupiter.api.Test;

class QuizRepositoryTest {

	private final QuizRepository quizRepository = new QuizRepository();

	@Test
	void givenSavedQuiz_WhenFindingById_ThenReturnsQuiz() {
		Quiz quiz = quizFixture(Instant.now().plusSeconds(3600));

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
		Quiz quiz = quizFixture(Instant.now().minusSeconds(1));
		quizRepository.save(quiz);

		assertThrows(QuizNotFoundException.class, () -> quizRepository.findById(quiz.id()));
	}

	private Quiz quizFixture(Instant expiresAt) {
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
								"Correct"))),
				expiresAt);
	}
}
