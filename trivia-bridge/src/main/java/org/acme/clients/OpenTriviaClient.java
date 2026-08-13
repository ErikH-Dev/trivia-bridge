package org.acme.clients;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.acme.dtos.QuestionsRequestDTO;
import org.acme.dtos.opentrivia.OpenTriviaQuizDTO;
import org.acme.entities.Question;
import org.acme.enums.QuestionDifficulty;
import org.acme.enums.QuestionType;
import org.acme.exceptions.NoQuestionsAvailableException;
import org.acme.exceptions.QuestionProviderException;
import org.acme.interfaces.IQuestionProvider;
import org.acme.mappers.QuizMapper;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OpenTriviaClient implements IQuestionProvider {

    private final OpenTriviaAPI openTriviaAPI;
    private final QuizMapper quizMapper;

    public OpenTriviaClient(@RestClient OpenTriviaAPI openTriviaAPI, QuizMapper quizMapper) {
        this.openTriviaAPI = openTriviaAPI;
        this.quizMapper = quizMapper;
    }

    @Override
    public List<Question> getQuestions(QuestionsRequestDTO request) {
        OpenTriviaQuizDTO response = openTriviaAPI.getQuestions(
                request.amount(),
                toCategoryParameter(request.category()),
                toDifficultyParameter(request.difficulty()),
                toTypeParameter(request.type()));

        if (response == null) {
            throw new QuestionProviderException("OpenTrivia returned no response");
        }

        if (response.responseCode() == 1) {
            throw new NoQuestionsAvailableException();
        }
        
        if (response.responseCode() > 1 ) {
            throw new QuestionProviderException("OpenTrivia returned response code " + response.responseCode());
        }

        return quizMapper.toQuestions(response);
    }

    private Optional<Integer> toCategoryParameter(int category) {
        if (category == 0) {
            return Optional.empty();
        }
        return Optional.of(category);
    }

    private Optional<String> toDifficultyParameter(QuestionDifficulty difficulty) {
        if (difficulty == QuestionDifficulty.ANY) {
            return Optional.empty();
        }
        return Optional.of(difficulty.name().toLowerCase(Locale.ROOT));
    }

    private Optional<String> toTypeParameter(QuestionType type) {
        if (type == QuestionType.ANY) {
            return Optional.empty();
        }
        return Optional.of(type.name().toLowerCase(Locale.ROOT));
    }

}
