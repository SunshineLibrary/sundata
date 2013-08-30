package models;

import com.google.code.morphia.annotations.Entity;
import play.modules.morphia.Model;

/**
 * id：“000ewjkr000192w” //一个属于单词的随机生成的uid
 * spelling：“name”        //<string> 拼写
 * meaning：“n. 名字”      //<string> 中文意思
 * unit_id：U4      //<int> 所属单元id
 * image：“img/name.jpg”      //一个可以直接读取的相对file_path
 * audio： “word/name.mp3”     //发音文件
 * phonetics：“neim”      //<string> 音标
 * group_：1      //<int> 在单元中的分组
 * proficiency_step：10      //<int> 每次做对增长的基础熟练度
 * example：“What's your name?” //<string>例句
 * example_meaning：“你叫什么名字?” //<string>例句翻译
 */
@Entity
public class Word extends Model {

    /**
     * <string> 拼写
     */
    public String spelling;
    /**
     * 中文意思
     */
    public String meaning;
    /**
     * 由spelling和unit_id共同确定的id
     */
    public String composite_id;
    /**
     * <int> 所属单元id
     */
    public String unit_id;
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
     * <int> 单元内的分组
     */
    public int group_;
    /**
     * <int> 每次做对增长的基础熟练度
     */
    public int proficiency_step;
    public String proficiency = "";
    public String continuous = "";
    /**
     * <string> 例句
     */
    public String example;
    /**
     * <string> 例句翻译
     */
    public String example_meaning;

    public Word(String spelling, String unit_id) {
        this.spelling = spelling;
        this.unit_id = unit_id;
        this.composite_id = getWordId(spelling, unit_id);
    }

    public static String getWordId(String spelling, String unit_id) {
        return spelling + ":" + unit_id;
    }

    public static Word createIfNotExists(String spelling, String unit_id) {
        Word word = Word.q("composite_id", Word.getWordId(spelling, unit_id)).first();
        if (word == null) {
            word = new Word(spelling, unit_id);
        }
        return word;
    }

    public String toString() {
        return composite_id;
    }

    public static class WordExample {
        public String body;

        public WordExample(String body) {
            this.body = body;
        }
    }

}
