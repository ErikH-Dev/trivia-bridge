package org.acme.services;

import java.time.Instant;
import java.util.UUID;

import org.acme.clients.OpenTriviaClient;
import org.acme.dtos.QuestionsRequestDTO;
import org.acme.dtos.response.QuizResponseDTO;
import org.acme.entities.Quiz;
import org.acme.interfaces.IQuestionProvider;
import org.acme.mappers.QuizMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuizService {
    private final IQuestionProvider questionProvider;
    private final QuizMapper quizMapper;

    public QuizService(OpenTriviaClient openTriviaClient, QuizMapper quizMapper) {
        this.questionProvider = openTriviaClient;
        this.quizMapper = quizMapper;
    }

    public QuizResponseDTO createQuiz(QuestionsRequestDTO request) {
        Quiz quiz = new Quiz(
                UUID.randomUUID(),
                questionProvider.get(request).questions(),
                Instant.now().plusSeconds(3600));

        return quizMapper.toDTO(quiz);
    }
}
