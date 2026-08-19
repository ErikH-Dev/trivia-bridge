package org.acme.interfaces;

import java.util.List;

import org.acme.dtos.QuestionsRequestDTO;
import org.acme.dtos.response.CategoryResponseDTO;
import org.acme.entities.Question;

public interface IQuestionProvider {
    List<Question> getQuestions(QuestionsRequestDTO questionRequest);

    List<CategoryResponseDTO> getCategories();
}
