package ua.jvic.workparser;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.jvic.workparser.parser.Parser;

import java.util.Map;

@Service
public class WorkParserService {

    private Parser[] parsers;

    public WorkParserService(@Autowired Parser[] parsers) {
        this.parsers = parsers;
    }

    public void parse() {
        for (Parser parser : parsers) {
            new Thread(() -> System.out.println(parser.getVacancies())).start();
        }
//        map.values().forEach(parser -> {
//            new Thread(() -> System.out.println(parser.getVacancies())).start();
//        });
    }
}
