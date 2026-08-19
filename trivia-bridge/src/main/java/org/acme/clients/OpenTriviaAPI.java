package org.acme.clients;

import java.util.Optional;

import org.acme.dtos.opentrivia.OpenTriviaQuizDTO;
import org.acme.exceptions.QuestionProviderException;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api.php")
@RegisterRestClient(configKey = "open-trivia-api")
@Produces(MediaType.APPLICATION_JSON)
public interface OpenTriviaAPI {
    @GET
    OpenTriviaQuizDTO getQuestions(
            @QueryParam("amount") int amount,
            @QueryParam("category") Optional<Integer> category,
            @QueryParam("difficulty") Optional<String> difficulty,
            @QueryParam("type") Optional<String> type);

    @ClientExceptionMapper
    static RuntimeException mapErrorResponse(Response response) {
        if (response.getStatus() >= 400) {
            return new QuestionProviderException(
                    "OpenTrivia returned HTTP status "
                            + response.getStatus());
        }

        return null;
    }
}
