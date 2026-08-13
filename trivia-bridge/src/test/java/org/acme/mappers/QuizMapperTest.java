package org.acme.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.acme.dtos.opentrivia.OpenTriviaQuestionDTO;
import org.acme.dtos.opentrivia.OpenTriviaQuizDTO;
import org.acme.dtos.response.QuizResponseDTO;
import org.acme.entities.Answer;
import org.acme.entities.Question;
import org.acme.entities.Quiz;
import org.acme.enums.QuestionDifficulty;
import org.acme.enums.QuestionType;
import org.acme.exceptions.QuestionProviderException;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class QuizMapperTest {

	@Inject
	QuizMapper mapper;

	@Test
	void givenInvalidOpenTriviaQuizDTO_WhenMapping_ThenThrowsQuestionProviderException() {
		OpenTriviaQuizDTO quizDTO = invalidOpenTriviaQuizDTOFixture();

		assertThrows(QuestionProviderException.class, () -> mapper.toQuestions(quizDTO));
	}

	@Test
	void givenOpenTriviaQuizDTOWithNoQuestions_WhenMapping_ThenThrowsQuestionProviderException() {
		OpenTriviaQuizDTO quizDTO = new OpenTriviaQuizDTO(1, List.of());

		assertThrows(QuestionProviderException.class, () -> mapper.toQuestions(quizDTO));
	}

	@Test
	void givenQuizEntity_WhenMapping_ThenReturnsValidQuizResponseDTO() {
		Quiz quiz = validQuizEntityFixture();

		QuizResponseDTO quizDTO = mapper.toDTO(quiz);

		assertNotNull(quizDTO);
		assertEquals(1, quizDTO.questions().size());
		assertEquals("Science & Nature", quizDTO.questions().get(0).category());
		assertEquals(QuestionDifficulty.MEDIUM, quizDTO.questions().get(0).difficulty());
		assertEquals(QuestionType.MULTIPLE, quizDTO.questions().get(0).type());
		assertEquals("What is H2O?", quizDTO.questions().get(0).question());
		assertEquals(
				Set.of("Water", "Hydrogen", "Oxygen", "Helium"),
				quizDTO.questions().get(0).options().stream()
						.map(answer -> answer.text())
						.collect(java.util.stream.Collectors.toSet()));
	}

	@Test
	void givenValidOpenTriviaQuizDTO_WhenMapping_ThenReturnsValidQuestionEntities() {
		OpenTriviaQuizDTO quizDTO = validOpenTriviaQuizDTOFixture();

		List<Question> questions = mapper.toQuestions(quizDTO);

		assertNotNull(questions);
		assertEquals(1, questions.size());
		Question questionEntity = questions.get(0);
		assertEquals(QuestionType.MULTIPLE, questionEntity.type());
		assertEquals(QuestionDifficulty.MEDIUM, questionEntity.difficulty());
		assertEquals("Science & Nature", questionEntity.category());
		assertEquals("What is H2O?", questionEntity.question());
		assertEquals("Water", questionEntity.correctAnswer().option());
		List<Answer> incorrectAnswers = questionEntity.incorrectAnswers();
		assertEquals(3, incorrectAnswers.size());
		assertEquals("Hydrogen", incorrectAnswers.get(0).option());
		assertEquals("Oxygen", incorrectAnswers.get(1).option());
		assertEquals("Helium", incorrectAnswers.get(2).option());
	}

	private OpenTriviaQuizDTO validOpenTriviaQuizDTOFixture() {
		return new OpenTriviaQuizDTO(
				0,
				List.of(new OpenTriviaQuestionDTO(
						QuestionType.MULTIPLE,
						QuestionDifficulty.MEDIUM,
						"Science & Nature",
						"What is H2O?",
						"Water",
						List.of("Hydrogen", "Oxygen", "Helium"))));
	}

	private OpenTriviaQuizDTO invalidOpenTriviaQuizDTOFixture() {
		return new OpenTriviaQuizDTO(
				2,
				List.of(new OpenTriviaQuestionDTO(
						QuestionType.MULTIPLE,
						QuestionDifficulty.MEDIUM,
						"Science & Nature",
						"What is H2O?",
						null,
						List.of("Hydrogen", "Oxygen", "Helium"))));
	}

	private Quiz validQuizEntityFixture() {
		return new Quiz(UUID.randomUUID(), List.of(new Question(
				UUID.randomUUID(),
				QuestionType.MULTIPLE,
				QuestionDifficulty.MEDIUM,
				"Science & Nature",
				"What is H2O?",
				List.of(new Answer(UUID.randomUUID(), "Hydrogen"),
						new Answer(UUID.randomUUID(), "Oxygen"),
						new Answer(UUID.randomUUID(), "Helium")),
				new Answer(UUID.randomUUID(), "Water"))), Instant.now().plusSeconds(3600));
	}
}
