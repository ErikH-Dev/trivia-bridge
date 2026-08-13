package org.acme.interfaces;

import java.util.List;

import org.acme.dtos.QuestionsRequestDTO;
import org.acme.entities.Question;

public interface IQuestionProvider {
    public List<Question> getQuestions(QuestionsRequestDTO questionRequest);
}
