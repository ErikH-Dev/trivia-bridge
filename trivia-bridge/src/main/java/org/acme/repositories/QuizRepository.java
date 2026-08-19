package org.acme.repositories;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.acme.entities.Quiz;
import org.acme.exceptions.QuizNotFoundException;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuizRepository {

    public static final String QUIZ_CACHE_NAME = "quizzes";

    private final CaffeineCache quizzes;

    public QuizRepository(@CacheName(QUIZ_CACHE_NAME) Cache cache) {
        this.quizzes = cache.as(CaffeineCache.class);
    }

    public void save(Quiz quiz) {
        quizzes.put(
                quiz.id(),
                CompletableFuture.completedFuture(quiz));
    }

    public Quiz findById(UUID quizId) {
        CompletableFuture<Object> cachedQuiz = quizzes.getIfPresent(quizId);

        if (cachedQuiz == null) {
            throw new QuizNotFoundException(
                    "Quiz not found or expired for ID: " + quizId);
        }

        Object value = cachedQuiz.join();

        if (!(value instanceof Quiz quiz)) {
            throw new IllegalStateException(
                    "Unexpected value stored in quiz cache");
        }

        return quiz;
    }
}