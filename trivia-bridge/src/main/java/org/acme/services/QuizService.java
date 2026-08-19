package org.acme.services;

import java.util.List;
import java.util.UUID;

import org.acme.dtos.AnswerCheckRequestDTO;
import org.acme.dtos.QuestionsRequestDTO;
import org.acme.dtos.QuizCheckRequestDTO;
import org.acme.dtos.response.QuestionCheckResponseDTO;
import org.acme.dtos.response.QuizCheckResponseDTO;
import org.acme.dtos.response.QuizResponseDTO;
import org.acme.entities.Question;
import org.acme.entities.Quiz;
import org.acme.exceptions.AnswerNotFoundException;
import org.acme.exceptions.InvalidQuizSubmissionException;
import org.acme.exceptions.QuestionNotFoundException;
import org.acme.interfaces.IQuestionProvider;
import org.acme.mappers.QuizMapper;
import org.acme.repositories.QuizRepository;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;

@ApplicationScoped
public class QuizService {
	private final IQuestionProvider questionProvider;
	private final QuizMapper quizMapper;
	private final QuizRepository quizRepository;

	private static final Logger LOG = Logger.getLogger(QuizService.class);

	public QuizService(IQuestionProvider questionProvider, QuizMapper quizMapper, QuizRepository quizRepository) {
		this.questionProvider = questionProvider;
		this.quizMapper = quizMapper;
		this.quizRepository = quizRepository;
	}

	public QuizResponseDTO createQuiz(@Valid QuestionsRequestDTO request) {
		Quiz quiz = new Quiz(
				UUID.randomUUID(),
				questionProvider.getQuestions(request));

		quizRepository.save(quiz);

		LOG.infof(
				"Created quiz: quizId=%s, questionCount=%d",
				quiz.id(),
				quiz.questions().size());

		return quizMapper.toQuizResponseDTO(quiz);
	}

	public QuizCheckResponseDTO checkAnswers(@Valid QuizCheckRequestDTO request) {

		Quiz quiz = quizRepository.findById(request.quizId());

		validateSubmission(quiz, request.answers());

		List<QuestionCheckResponseDTO> results = request.answers().stream()
				.map(answer -> checkAnswer(quiz, answer))
				.toList();

		int correctAnswerCount = Math.toIntExact(
				results.stream()
						.filter(QuestionCheckResponseDTO::correct)
						.count());

		LOG.infof(
				"Checked quiz: quizId=%s, correctAnswers=%d, totalQuestions=%d",
				quiz.id(),
				correctAnswerCount,
				quiz.questions().size());

		return new QuizCheckResponseDTO(
				quiz.id(),
				correctAnswerCount,
				quiz.questions().size(),
				results);
	}

	private QuestionCheckResponseDTO checkAnswer(
			Quiz quiz,
			AnswerCheckRequestDTO submittedAnswer) {

		Question question = quiz.questions().stream()
				.filter(candidate -> candidate.id().equals(submittedAnswer.questionId()))
				.findFirst()
				.orElseThrow(() -> new QuestionNotFoundException(
						submittedAnswer.questionId()));

		if (!question.containsAnswer(submittedAnswer.answerId())) {
			throw new AnswerNotFoundException(
					submittedAnswer.answerId());
		}

		return new QuestionCheckResponseDTO(
				question.id(),
				submittedAnswer.answerId(),
				question.correctAnswer().id(),
				question.isCorrect(submittedAnswer.answerId()));
	}

	private void validateSubmission(Quiz quiz, List<AnswerCheckRequestDTO> submittedAnswers) {

		long uniqueQuestionCount = submittedAnswers.stream()
				.map(AnswerCheckRequestDTO::questionId)
				.distinct()
				.count();

		if (uniqueQuestionCount != submittedAnswers.size() || submittedAnswers.size() != quiz.questions().size()) {
			throw new InvalidQuizSubmissionException(
					"Submit exactly one answer for every quiz question.");
		}
	}
}
