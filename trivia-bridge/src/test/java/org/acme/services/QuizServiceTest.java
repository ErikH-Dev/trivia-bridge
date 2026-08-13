package org.acme.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.acme.clients.OpenTriviaClient;
import org.acme.dtos.AnswerCheckRequestDTO;
import org.acme.dtos.QuizCheckRequestDTO;
import org.acme.dtos.response.QuestionCheckResponseDTO;
import org.acme.dtos.response.QuizCheckResponseDTO;
import org.acme.entities.Answer;
import org.acme.entities.Question;
import org.acme.entities.Quiz;
import org.acme.enums.QuestionDifficulty;
import org.acme.enums.QuestionType;
import org.acme.exceptions.AnswerNotFoundException;
import org.acme.exceptions.QuestionNotFoundException;
import org.acme.mappers.QuizMapper;
import org.acme.repositories.QuizRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

	private static final UUID QUIZ_ID = UUID.fromString("00000000-0000-0000-0000-000000000100");
	private static final UUID FIRST_QUESTION_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");
	private static final UUID SECOND_QUESTION_ID = UUID.fromString("00000000-0000-0000-0000-000000000102");
	private static final UUID FIRST_CORRECT_ANSWER_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
	private static final UUID FIRST_WRONG_ANSWER_ID = UUID.fromString("00000000-0000-0000-0000-000000000202");
	private static final UUID SECOND_CORRECT_ANSWER_ID = UUID.fromString("00000000-0000-0000-0000-000000000203");
	private static final UUID SECOND_WRONG_ANSWER_ID = UUID.fromString("00000000-0000-0000-0000-000000000204");

	@Mock
	OpenTriviaClient openTriviaClient;

	@Mock
	QuizMapper quizMapper;

	@Mock
	QuizRepository quizRepository;

	@InjectMocks
	QuizService quizService;

	@Test
	void givenCorrectAndIncorrectAnswers_WhenCheckingAnswers_ThenReturnsQuestionResultsAndScore() {
		Quiz quiz = validQuizEntityFixture();
		QuizCheckRequestDTO request = new QuizCheckRequestDTO(
				QUIZ_ID,
				List.of(
						new AnswerCheckRequestDTO(FIRST_QUESTION_ID, FIRST_CORRECT_ANSWER_ID),
						new AnswerCheckRequestDTO(SECOND_QUESTION_ID, SECOND_WRONG_ANSWER_ID)));

		when(quizRepository.findById(QUIZ_ID)).thenReturn(quiz);

		QuizCheckResponseDTO result = quizService.checkAnswers(request);

		assertEquals(
				new QuizCheckResponseDTO(
						QUIZ_ID,
						1,
						2,
						List.of(
								new QuestionCheckResponseDTO(
										FIRST_QUESTION_ID,
										FIRST_CORRECT_ANSWER_ID,
										FIRST_CORRECT_ANSWER_ID,
										true),
								new QuestionCheckResponseDTO(
										SECOND_QUESTION_ID,
										SECOND_WRONG_ANSWER_ID,
										SECOND_CORRECT_ANSWER_ID,
										false))),
				result);
	}

	@Test
	void givenUnknownQuestion_WhenCheckingAnswers_ThenThrowsQuestionNotFoundException() {
		Quiz quiz = validQuizEntityFixture();
		UUID unknownQuestionId = UUID.fromString("00000000-0000-0000-0000-000000000999");
		QuizCheckRequestDTO request = new QuizCheckRequestDTO(
				QUIZ_ID,
				List.of(new AnswerCheckRequestDTO(unknownQuestionId, FIRST_CORRECT_ANSWER_ID)));

		when(quizRepository.findById(QUIZ_ID)).thenReturn(quiz);

		assertThrows(QuestionNotFoundException.class, () -> quizService.checkAnswers(request));
	}

	@Test
	void givenAnswerFromAnotherQuestion_WhenCheckingAnswers_ThenThrowsAnswerNotFoundException() {
		Quiz quiz = validQuizEntityFixture();
		QuizCheckRequestDTO request = new QuizCheckRequestDTO(
				QUIZ_ID,
				List.of(new AnswerCheckRequestDTO(FIRST_QUESTION_ID, SECOND_CORRECT_ANSWER_ID)));

		when(quizRepository.findById(QUIZ_ID)).thenReturn(quiz);

		assertThrows(AnswerNotFoundException.class, () -> quizService.checkAnswers(request));
	}

	private Quiz validQuizEntityFixture() {
		return new Quiz(
				QUIZ_ID,
				List.of(
						new Question(
								FIRST_QUESTION_ID,
								QuestionType.MULTIPLE,
								QuestionDifficulty.EASY,
								"General Knowledge",
								"First question?",
								List.of(new Answer(FIRST_WRONG_ANSWER_ID, "Wrong")),
								new Answer(FIRST_CORRECT_ANSWER_ID, "Correct")),
						new Question(
								SECOND_QUESTION_ID,
								QuestionType.MULTIPLE,
								QuestionDifficulty.MEDIUM,
								"Science",
								"Second question?",
								List.of(new Answer(SECOND_WRONG_ANSWER_ID, "Wrong")),
								new Answer(SECOND_CORRECT_ANSWER_ID, "Correct"))),
				Instant.now().plusSeconds(3600));
	}
}
