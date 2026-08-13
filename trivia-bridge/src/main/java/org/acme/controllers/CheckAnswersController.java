package org.acme.controllers;

import org.acme.dtos.QuizCheckRequestDTO;
import org.acme.services.QuizService;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/checkanswers")
public class CheckAnswersController {
    private final QuizService quizService;

    public CheckAnswersController(QuizService quizService) {
        this.quizService = quizService;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAnswers(@Valid QuizCheckRequestDTO quizCheckRequest) {
        return Response.ok(quizService.checkAnswers(quizCheckRequest)).build();
    }
}
