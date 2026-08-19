package org.acme.exceptions.mappers;

import org.acme.exceptions.AnswerNotFoundException;
import org.acme.exceptions.ErrorResponse;
import org.acme.exceptions.InvalidQuizSubmissionException;
import org.acme.exceptions.NoQuestionsAvailableException;
import org.acme.exceptions.QuestionNotFoundException;
import org.acme.exceptions.QuestionProviderException;
import org.acme.exceptions.QuizNotFoundException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import jakarta.ws.rs.core.Response;

public class ExceptionMappers {

	@ServerExceptionMapper
	public RestResponse<ErrorResponse> noQuestionsAvailable(
			NoQuestionsAvailableException exception) {

		return RestResponse.status(
				Response.Status.NOT_FOUND,
				new ErrorResponse(
						"NO_QUESTIONS_AVAILABLE",
						exception.getMessage()));
	}

	@ServerExceptionMapper
	public RestResponse<ErrorResponse> quizNotFound(
			QuizNotFoundException exception) {

		return RestResponse.status(
				Response.Status.NOT_FOUND,
				new ErrorResponse(
						"QUIZ_NOT_FOUND",
						exception.getMessage()));
	}

	@ServerExceptionMapper
	public RestResponse<ErrorResponse> questionNotFound(
			QuestionNotFoundException exception) {

		return RestResponse.status(
				Response.Status.NOT_FOUND,
				new ErrorResponse(
						"QUESTION_NOT_FOUND",
						exception.getMessage()));
	}

	@ServerExceptionMapper
	public RestResponse<ErrorResponse> answerNotFound(
			AnswerNotFoundException exception) {

		return RestResponse.status(
				Response.Status.NOT_FOUND,
				new ErrorResponse(
						"ANSWER_NOT_FOUND",
						exception.getMessage()));
	}

	@ServerExceptionMapper
	public RestResponse<ErrorResponse> questionProviderException(
		QuestionProviderException exception) {
		return RestResponse.status(
				Response.Status.BAD_GATEWAY,
				new ErrorResponse(
						"QUESTION_PROVIDER_ERROR",
						"Unable to retrieve questions"));
	}

	@ServerExceptionMapper
	public RestResponse<ErrorResponse> invalidSubmission(
		InvalidQuizSubmissionException exception) {
		return RestResponse.status(
				Response.Status.BAD_REQUEST,
				new ErrorResponse(
						"INVALID_QUIZ_SUBMISSION",
						exception.getMessage()));
	}
}