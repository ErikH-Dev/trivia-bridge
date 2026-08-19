package org.acme.controllers;

import org.acme.dtos.response.CategoriesResponseDTO;
import org.acme.interfaces.IQuestionProvider;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoriesController {

    private final IQuestionProvider questionProvider;

    public CategoriesController(IQuestionProvider questionProvider) {
        this.questionProvider = questionProvider;
    }

    @GET
    public CategoriesResponseDTO getCategories() {
        return new CategoriesResponseDTO(questionProvider.getCategories());
    }
}
