package ua.jvic.workparser.parser;

import ua.jvic.workparser.model.Vacancy;

import java.util.List;

public interface Parser {
    List<Vacancy> getVacancies();
}
