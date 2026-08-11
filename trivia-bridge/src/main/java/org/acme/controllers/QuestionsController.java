package org.acme.controllers;

import org.acme.dtos.QuestionsRequestDTO;
import org.acme.services.QuizService;

import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/questions")
public class QuestionsController {

    private final QuizService quizService;

    public QuestionsController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestions(@BeanParam @Valid QuestionsRequestDTO questionRequest) {
        return Response.ok(quizService.createQuiz(questionRequest)).build();
    }
}
