package org.acme.mappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.acme.dtos.opentrivia.OpenTriviaQuestionDTO;
import org.acme.dtos.opentrivia.OpenTriviaQuizDTO;
import org.acme.dtos.response.QuizAnswerResponseDTO;
import org.acme.dtos.response.QuizQuestionResponseDTO;
import org.acme.dtos.response.QuizResponseDTO;
import org.acme.entities.Answer;
import org.acme.entities.Question;
import org.acme.entities.Quiz;
import org.acme.exceptions.QuestionProviderException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Validator;

@ApplicationScoped
public class QuizMapper {

    private final Validator validator;

    public QuizMapper(Validator validator) {
        this.validator = validator;
    }

    public List<Question> toQuestions(OpenTriviaQuizDTO quizResponseDTO) {
        if (quizResponseDTO == null || !validator.validate(quizResponseDTO).isEmpty()) {
            throw new QuestionProviderException("OpenTrivia returned invalid question data");
        }

        return quizResponseDTO.results().stream()
                .map(this::toQuestion)
                .toList();
    }

    private Question toQuestion(OpenTriviaQuestionDTO openTriviaQuestionDTO) {
        UUID questionId = UUID.randomUUID();
        return new Question(
            questionId,
            openTriviaQuestionDTO.type(),
            openTriviaQuestionDTO.difficulty(),
            openTriviaQuestionDTO.category(),
            openTriviaQuestionDTO.question(),
            openTriviaQuestionDTO.incorrectAnswers().stream()
                .map(this::toAnswer)
                .toList(),
            toAnswer(openTriviaQuestionDTO.correctAnswer())
        );
    }

    private Answer toAnswer(String option) {
        UUID answerId = UUID.randomUUID();
        return new Answer(answerId, option);
    }

    public QuizResponseDTO toDTO(Quiz quiz) {
        return new QuizResponseDTO(
            quiz.id(),
            quiz.questions().stream()
                .map(this::toDTO)
                .toList()
        );
    }

    private QuizQuestionResponseDTO toDTO(Question question) {
        List<Answer> allAnswers = new ArrayList<>(question.incorrectAnswers());
        allAnswers.add(question.correctAnswer());
        Collections.shuffle(allAnswers);

        return new QuizQuestionResponseDTO(
            question.id(),
            question.category(),
            question.difficulty(),
            question.type(),
            question.question(),
            allAnswers.stream()
                .map(this::toDTO)
                .toList()
        );
    }

    private QuizAnswerResponseDTO toDTO(Answer answer) {
        return new QuizAnswerResponseDTO(
            answer.id(),
            answer.option()
        );
    }
}
