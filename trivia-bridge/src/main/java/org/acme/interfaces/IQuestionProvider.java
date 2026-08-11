package org.acme.interfaces;

import org.acme.dtos.QuestionsRequestDTO;
import org.acme.entities.Quiz;

public interface IQuestionProvider {
    public Quiz get(QuestionsRequestDTO questionRequest);
}
