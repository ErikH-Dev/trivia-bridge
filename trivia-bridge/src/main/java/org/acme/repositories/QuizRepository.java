package org.acme.repositories;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.acme.entities.Quiz;
import org.acme.exceptions.QuizNotFoundException;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuizRepository {
    private final ConcurrentMap<UUID, Quiz> quizzes = new ConcurrentHashMap<>();

    public void save(Quiz quiz) {
        quizzes.put(quiz.id(), quiz);
    }

    public Quiz findById(UUID quizId) {
        Quiz quiz = quizzes.get(quizId);

        if (quiz == null) {
            throw new QuizNotFoundException("Quiz not found for ID: " + quizId);
        }

        if (quiz.expiresAt().isBefore(java.time.Instant.now())) {
            quizzes.remove(quizId);
            throw new QuizNotFoundException("Quiz has expired for ID: " + quizId);
        }

        return quiz;
    }
}
