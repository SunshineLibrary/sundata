package controllers;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVEntryFilter;
import com.googlecode.jcsv.reader.CSVEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import models.Phonetics;
import models.Problem;
import models.Word;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.mvc.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Application extends Controller {

    public static void deleteAll() {
        //MorphiaFixtures.deleteDatabase();
    }

    public static void deleteAllProblems() {
        Problem.deleteAll();
        renderText("complete deleteAllProblems");
    }

    public static void deleteAllPhonetics() {
        Phonetics.deleteAll();
        renderText("complete deleteAllPhonetics");
    }

    public static void deleteAllWords() {
        Word.deleteAll();
        renderText("complete deleteAllWords");
    }

    public static void index(File csv) throws IOException {
        render();
    }

    public static void completeWord() {
        File csv = new File("/Users/fxp/Downloads/completeword.csv");
    }

    public static void words() {
        renderJSON(Word.findAll());
    }

    public static void problems() {
        renderJSON(Problem.findAll());
    }

    public static void phonetics() {
        renderJSON(Phonetics.findAll());
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
                new CSVReaderBuilder<Problem>(reader)
                        .strategy(CSVStrategy.UK_DEFAULT)
                        .entryFilter(new ProblemFilter())
                        .entryParser(new ProblemEntryParser()).build();
        List<Problem> problems = csvPersonReader.readAll();
        renderJSON(problems);
    }

    public static void addPhonetics(File csv) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(csv));
        reader.readLine();

        CSVReader<Phonetics> csvPersonReader =
                new CSVReaderBuilder<Phonetics>(reader).strategy(CSVStrategy.UK_DEFAULT)
                        .entryParser(new PhoneticEntryParser()).build();
        List<Phonetics> phonetics = csvPersonReader.readAll();
        renderJSON(phonetics);

    }

    public static class PhoneticEntryParser implements CSVEntryParser<Phonetics> {
        public Phonetics parseEntry(String... data) {
            String spelling = data[0];
            int part = Integer.valueOf(data[1]);
            Phonetics p = Phonetics.createIfNotExists(spelling, part);
            p.spelling_part = data[2].trim();
            p.phonetic_part = data[3];
            p.audio_part = data[4];
            p = p.save();
            Logger.info("Saved a phonetics.%s", p);
            return p;
        }
    }

    public static class WordEntryParser implements CSVEntryParser<Word> {

        public Word parseEntry(String... data) {
            String spelling = data[0].trim();
            String unit_id = data[2];
            Word word = Word.createIfNotExists(spelling, unit_id);
            word.spelling = spelling;
            word.unit_id = unit_id;
            word.meaning = data[1];
            word.image = data[3];
            word.audio = data[4];
            word.phonetics = data[5];
            word.group_ = Integer.valueOf(data[6]);
            word.proficiency_step = Integer.valueOf(data[7]);
            word.example = data[8];
            word.example_meaning = data[9];

            word = word.save();
            Logger.info("Saved a word.%s", word.getIdAsStr());
            return word;
        }
    }

    public static class ProblemFilter implements CSVEntryFilter<Problem> {
        @Override
        public boolean match(Problem problem) {
            return (StringUtils.isEmpty(problem.type));
        }
    }

    public static class ProblemEntryParser implements CSVEntryParser<Problem> {

        public Problem parseEntry(String... data) {
            Problem ret = new Problem();
            String wordSpelling = data[2].trim();
            String unit_id = data[0];
            Word word = Word.q("composite_id", Word.getWordId(wordSpelling, unit_id)).first();
            if (word == null) {
                Logger.warn("Problem not imported. Word not exists,%s,%s.", wordSpelling, unit_id);
                return new Problem();
            }
            ret.spelling = wordSpelling;
            ret.unit_id = unit_id;
            ret.word_composite_id = word.composite_id;
            ret.type = data[1];
            ret.pool = data[3];
            ret.title = data[4];
            ret.body = data[5];
            ret.audio = data[6];
            ret.image = data[7];

            Logger.info("problem type:" + ret.type);
            if (!"填空".equals(ret.type)) {
                ret.answer = data[8];
                ret.wrong_select1 = data[9];
                ret.wrong_select2 = data[10];
                ret.wrong_select3 = data[11];
//                ret.choices = new ArrayList<Problem.ProblemChoice>();
//                Problem.ProblemChoice rightOne = new Problem.ProblemChoice(data[8], true);
//                ret.choices.add(rightOne);
//                Problem.ProblemChoice wrongOne = null;
//                for (int i = 9; i < 12; i++) {
//                    wrongOne = new Problem.ProblemChoice(data[i], false);
//                    ret.choices.add(wrongOne);
//                }
            } else {
                ret.answer = data[8];
            }
            ret = ret.save();
            Logger.info("Saved a problem.%s", ret.word_composite_id);
            return ret;
        }

    }
}
