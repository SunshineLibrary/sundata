package controllers;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import models.Problem;
import models.Word;
import play.Logger;
import play.mvc.Controller;
import play.test.MorphiaFixtures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Application extends Controller {

    public static void deleteAll() {
        MorphiaFixtures.deleteDatabase();
        renderText("complete delete all");
    }

    public static void index(File csv) throws IOException {
        render();
    }

    public static void addWords(File csv) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(csv));
        reader.readLine();

        CSVReader<Word> csvPersonReader =
                new CSVReaderBuilder<Word>(reader)
                        .strategy(CSVStrategy.UK_DEFAULT)
                        .entryParser(new WordEntryParser()).build();
        List<Word> words = csvPersonReader.readAll();
        renderText("completed. imported " + words.size() + " words");
    }

    public static void addProblems(File csv) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(csv));
        reader.readLine();

        CSVReader<Problem> csvPersonReader =
                new CSVReaderBuilder<Problem>(reader).strategy(CSVStrategy.UK_DEFAULT)
                        .entryParser(new ProblemEntryParser()).build();
        List<Problem> problems = csvPersonReader.readAll();
        renderJSON(problems);
    }

    public static class WordEntryParser implements CSVEntryParser<Word> {

        public Word parseEntry(String... data) {
            String spelling = data[0];
            String unit_id = data[2];
            Word word = Word.createIfNotExists(spelling, unit_id);
            word.meaning = data[1];
            word.image = data[3];
            word.audio = data[4];
            word.phonetics = data[5];
            word.group = Integer.valueOf(data[6]);
            word.proficiency_step = Integer.valueOf(data[7]);
            word.example = data[8];
            word.example_meaning = data[9];

            word = word.save();
            Logger.info("Saved a word.%s", word.getIdAsStr());
            return word;
        }
    }

    public static class ProblemEntryParser implements CSVEntryParser<Problem> {

        public Problem parseEntry(String... data) {
            Problem ret = new Problem();
            String wordSpelling = data[0];
            String unit_id = data[1];
            Word word = Word.q("composite_id", Word.getWordId(wordSpelling, unit_id)).first();
            if (word == null) {
                Logger.warn("Problem not imported. Word not exists,%s,%s." + wordSpelling, unit_id);
                return null;
            }
            ret.word_composite_id = word.composite_id;
            ret.type = data[5];
            ret.title = data[7];
            ret.body = data[8];
            ret.audio = data[9];
            ret.image = data[10];

            Problem.ProblemChoice rightOne = new Problem.ProblemChoice(data[11], true);
            ret.choices.add(rightOne);

            if (!"填空".equals(ret.type)) {
                Problem.ProblemChoice wrongOne = null;
                for (int i = 12; i < 15; i++) {
                    wrongOne = new Problem.ProblemChoice(data[i], false);
                    ret.choices.add(wrongOne);
                }
            }
            ret = ret.save();
            Logger.info("Saved a problem.%s", ret.word_composite_id);
            return ret;
        }

    }

}