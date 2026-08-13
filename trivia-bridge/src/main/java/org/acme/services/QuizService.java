package org.acme.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.acme.clients.OpenTriviaClient;
import org.acme.dtos.AnswerCheckRequestDTO;
import org.acme.dtos.QuestionsRequestDTO;
import org.acme.dtos.QuizCheckRequestDTO;
import org.acme.dtos.response.QuestionCheckResponseDTO;
import org.acme.dtos.response.QuizCheckResponseDTO;
import org.acme.dtos.response.QuizResponseDTO;
import org.acme.entities.Question;
import org.acme.entities.Quiz;
import org.acme.exceptions.AnswerNotFoundException;
import org.acme.exceptions.QuestionNotFoundException;
import org.acme.interfaces.IQuestionProvider;
import org.acme.mappers.QuizMapper;
import org.acme.repositories.QuizRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;

@ApplicationScoped
public class QuizService {
    private final IQuestionProvider questionProvider;
    private final QuizMapper quizMapper;
    private final QuizRepository quizRepository;

    public QuizService(OpenTriviaClient openTriviaClient, QuizMapper quizMapper, QuizRepository quizRepository) {
        this.questionProvider = openTriviaClient;
        this.quizMapper = quizMapper;
        this.quizRepository = quizRepository;
    }

    public QuizResponseDTO createQuiz(@Valid QuestionsRequestDTO request) {
        Quiz quiz = new Quiz(
                UUID.randomUUID(),
                questionProvider.get(request).questions(),
                Instant.now().plusSeconds(3600));

        quizRepository.save(quiz);

        return quizMapper.toDTO(quiz);
    }

    public QuizCheckResponseDTO checkAnswers(@Valid QuizCheckRequestDTO request) {
        Quiz quiz = quizRepository.findById(request.quizId());

        List<QuestionCheckResponseDTO> results = request.answers().stream()
                .map(answer -> checkAnswer(quiz, answer))
                .toList();

        int correctAnswerCount = (int) results.stream().filter(QuestionCheckResponseDTO::correct).count();

        return new QuizCheckResponseDTO(
                quiz.id(),
                correctAnswerCount,
                quiz.questions().size(),
                results);
    }

    private QuestionCheckResponseDTO checkAnswer(Quiz quiz, AnswerCheckRequestDTO submittedAnswer) {
        Question question = quiz.questions().stream()
                .filter(q -> q.id().equals(submittedAnswer.questionId()))
                .findFirst()
                .orElseThrow(() -> new QuestionNotFoundException(submittedAnswer.questionId()));

        if (!question.containsAnswer(submittedAnswer.answerId())) {
            throw new AnswerNotFoundException(submittedAnswer.answerId());
        }

        return new QuestionCheckResponseDTO(
                question.id(),
                submittedAnswer.answerId(),
                question.correctAnswer().id(),
                question.isCorrect(submittedAnswer.answerId())
        );
    }
}
