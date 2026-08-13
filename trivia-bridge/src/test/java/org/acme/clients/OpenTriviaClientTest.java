package org.acme.clients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.dtos.QuestionsRequestDTO;
import org.acme.dtos.opentrivia.OpenTriviaQuestionDTO;
import org.acme.dtos.opentrivia.OpenTriviaQuizDTO;
import org.acme.entities.Answer;
import org.acme.entities.Question;
import org.acme.enums.QuestionDifficulty;
import org.acme.enums.QuestionType;
import org.acme.exceptions.NoQuestionsAvailableException;
import org.acme.exceptions.QuestionProviderException;
import org.acme.mappers.QuizMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenTriviaClientTest {

	@Mock
	OpenTriviaAPI openTriviaAPI;

	@Mock
	QuizMapper quizMapper;

	@InjectMocks
	private OpenTriviaClient client;

	@Test
	void givenQuestionRequest_WhenGettingQuestions_ThenOmitsOptionalParameters() {
		QuestionsRequestDTO request = anyQuestionsRequestDTOFixture();
		OpenTriviaQuizDTO response = validOpenTriviaQuizDTOFixture();
		List<Question> expectedQuestions = validQuestionEntitiesFixture();

		when(openTriviaAPI.getQuestions(10, Optional.empty(), Optional.empty(), Optional.empty())).thenReturn(response);
		when(quizMapper.toQuestions(response)).thenReturn(expectedQuestions);

		List<Question> questions = client.getQuestions(request);

		assertEquals(expectedQuestions, questions);
		verify(openTriviaAPI).getQuestions(10, Optional.empty(), Optional.empty(), Optional.empty());
		verify(quizMapper).toQuestions(response);
	}

	@Test
	void givenQuestionRequest_WhenGettingQuestions_ThenConvertsParameters() {
		QuestionsRequestDTO request = specificQuestionsRequestDTOFixture();
		OpenTriviaQuizDTO response = validOpenTriviaQuizDTOFixture();
		List<Question> expectedQuestions = validQuestionEntitiesFixture();

		when(openTriviaAPI.getQuestions(10, Optional.of(17), Optional.of("medium"), Optional.of("multiple")))
				.thenReturn(response);
		when(quizMapper.toQuestions(response)).thenReturn(expectedQuestions);

		List<Question> questions = client.getQuestions(request);

		assertEquals(expectedQuestions, questions);
		verify(openTriviaAPI).getQuestions(10, Optional.of(17), Optional.of("medium"), Optional.of("multiple"));
		verify(quizMapper).toQuestions(response);
	}

	@Test
	void givenNoQuestionsResponse_WhenGettingQuestions_ThenThrowsNoQuestionsAvailableException() {
		QuestionsRequestDTO request = specificQuestionsRequestDTOFixture();
		OpenTriviaQuizDTO response = new OpenTriviaQuizDTO(1, List.of());

		when(openTriviaAPI.getQuestions(
				10, Optional.of(17), Optional.of("medium"), Optional.of("multiple")))
				.thenReturn(response);

		assertThrows(NoQuestionsAvailableException.class, () -> client.getQuestions(request));
		verifyNoInteractions(quizMapper);
	}

	@Test
	void givenProviderErrorResponse_WhenGettingQuestions_ThenThrowsQuestionProviderException() {
		QuestionsRequestDTO request = specificQuestionsRequestDTOFixture();
		OpenTriviaQuizDTO response = new OpenTriviaQuizDTO(2, List.of());

		when(openTriviaAPI.getQuestions(10, Optional.of(17), Optional.of("medium"), Optional.of("multiple")))
				.thenReturn(response);

		assertThrows(QuestionProviderException.class, () -> client.getQuestions(request));
		verifyNoInteractions(quizMapper);
	}

	@Test
	void givenNoProviderResponse_WhenGettingQuestions_ThenThrowsQuestionProviderException() {
		QuestionsRequestDTO request = specificQuestionsRequestDTOFixture();

		when(openTriviaAPI.getQuestions(10, Optional.of(17), Optional.of("medium"), Optional.of("multiple")))
				.thenReturn(null);

		assertThrows(QuestionProviderException.class, () -> client.getQuestions(request));
		verifyNoInteractions(quizMapper);
	}

	private QuestionsRequestDTO anyQuestionsRequestDTOFixture() {
		return new QuestionsRequestDTO(
				10,
				0,
				QuestionDifficulty.ANY,
				QuestionType.ANY);
	}

	private QuestionsRequestDTO specificQuestionsRequestDTOFixture() {
		return new QuestionsRequestDTO(
				10,
				17,
				QuestionDifficulty.MEDIUM,
				QuestionType.MULTIPLE);
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

	private List<Question> validQuestionEntitiesFixture() {
		return List.of(new Question(
						UUID.randomUUID(),
						QuestionType.MULTIPLE,
						QuestionDifficulty.MEDIUM,
						"Science & Nature",
						"What is H2O?",
						List.of(
								new Answer(UUID.randomUUID(), "Hydrogen"),
								new Answer(UUID.randomUUID(), "Oxygen"),
								new Answer(UUID.randomUUID(), "Helium")),
						new Answer(UUID.randomUUID(), "Water")));
	}
}
