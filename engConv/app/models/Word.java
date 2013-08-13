package models;

import org.hibernate.annotations.GenericGenerator;
import play.db.jpa.GenericModel;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * unit_id：4      //<int> 所属单元id
 * spelling：“name”        //<string> 拼写
 * meaning：“n. 名字”      //<string> 中文意思
 * image：“img/name.jpg”      //我们还在讨论本地文件管理的方案，可以先把这里理解为一个可以直接读取的相对file_path
 * audio： “word/name.mp3”     //发音文件
 * phonetics：“neim”      //<string> 音标
 * proficiency_step：10      //<int> 每次做对增长的基础熟练度
 * examples [     //例句们
 * {
 * body      //<html> 包含三个tag，例句文本(text)，例句发音(audio)，例句中文意思(translation)
 * <audio src='sentence/name.mp3'></audio>
 * <p>What is your name?</p>
 * <p>你叫什么名字？</p>
 * }
 * ]
 * pronunciation [
 * （包括单词的拆分、音标的拆分、以及每部分的发音，现在是怎么在json里存的？下面这种方式可以吗？或者把单词拆分，音标拆分和发音拆分各存成一个list？）
 * {
 * word_part：[“na”]
 * phonetics_part：[“nei”]
 * audio_part：“<audio src='phonetics/nei_f.mp3'></audio>”
 * },
 * {
 * word_part：[“me”]
 * phonetics_part：[“m”]
 * audio_part：“<audio src='phonetics/m.mp3'></audio>”
 * }
 * ]
 * }
 */
@Entity
public class Word extends GenericModel {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String id;
    /**
     * <int> 所属单元id
     */
    public String unit_id;
    /**
     * <string> 拼写
     */
    public String spelling;
    /**
     * 中文意思
     */
    public String meaning;
    /**
     * 我们还在讨论本地文件管理的方案，可以先把这里理解为一个可以直接读取的相对file_path
     */
    public String image;
    /**
     * 发音文件
     */
    public String audio;
    /**
     * <string> 音标
     */
    public String phonetics;
    /**
     * <int> 每次做对增长的基础熟练度
     */
    public int proficiency_step;
    /**
     * 例句们
     */
    @OneToMany
    public List<WordExample> examples = new ArrayList<WordExample>();
    @OneToMany
    public List<WordPronunciation> pronunciations = new ArrayList<WordPronunciation>();

    public Word(String spelling, String meaning) {
        this.spelling = spelling;
        this.meaning = meaning;
    }

    @Entity
    public static class WordExample extends Model {
        public String body;
    }

    @Entity
    public static class WordPronunciation extends Model {
        @ElementCollection
        public List<String> word_part = new ArrayList<String>();
        @ElementCollection
        public List<String> phonetics_part = new ArrayList<String>();
        public String audio_part;
    }
}
