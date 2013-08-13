package controllers;

import com.google.gson.Gson;
import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import models.Problem;
import models.Word;
import play.mvc.Controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class Application extends Controller {

    public static void index(File csv) throws IOException {
        Reader reader = new FileReader(csv);

        CSVReader<Problem> csvPersonReader =
                new CSVReaderBuilder<Problem>(reader).strategy(CSVStrategy.UK_DEFAULT)
                        .entryParser(new ProblemEntryParser()).build();
        List<Problem> problems = csvPersonReader.readAll();

        System.out.println("problems:" + new Gson().toJson(problems));

        List<Word> words = Word.findAll();
        System.out.println("words:" + new Gson().toJson(words));

        renderJSON(words);
    }

    public static class ProblemEntryParser implements CSVEntryParser<Problem> {

        public Problem parseEntry(String... data) {
            Problem ret = new Problem();
            String wordSpelling = data[0];
            Word word = Word.find("spelling", data[0]).first();
            if (word == null) {
                word = new Word(wordSpelling, data[1]);
                word = word.save();
            }
            ret.word_id = word.id;
            ret.type = data[5];
            ret.body = data[8];
            ret.title = data[7];

            Problem.ProblemChoice rightOne = new Problem.ProblemChoice(data[11], true).save();
            ret.choices.add(rightOne);

            Problem.ProblemChoice wrongOne = null;
            for (int i = 12; i < 15; i++) {
                wrongOne = new Problem.ProblemChoice(data[i], false).save();
                ret.choices.add(wrongOne);
            }

            return ret.save();
        }

    }


}