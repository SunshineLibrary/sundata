package models;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import play.modules.morphia.Model;

import java.util.List;

/**
 * body：“<audio src='word/name.mp3'></audio><p>name</p><img src='img/name.jpg'/>”      //<html> 题干，如果有图片或音频会直接以tag嵌入其中
 * word_id：171     //<int> 是关于哪个单词的（或者没有，即不关联任何单词）
 * pool：“review”      //<string> 复习还是练习（"practice"用于练习, "review"用于复习或挑战, "both"都行）
 * type：“单选”    //<string> 题型（“单选”, “多选”, “填空”, “图片”）
 * choices [     //选择题的选项
 * {
 * id：1      //<int> 每个choice有自己的uid
 * body：“n. 名字”      //<html> 选项文字，也是个html，如果有图片或音频会直接以tag嵌入其中
 * is_answer：true      //<boolean> 是否正确答案
 * },
 * {
 * id：2
 * body：“n. 猫”
 * is_answer：false
 * },
 * {
 * id：3
 * body：“n.小猫”
 * is_answer：false
 * },
 * {
 * id：4
 * body：“n. 呵呵”
 * is_answer：false
 * }
 * ]
 * explanation：“hi<img src='img/name.jpg'/>”      //<html> 题目解释（如果做错了会解释），可能没有
 * hint：“What’s your name?”      //<html> 提示，可能没有
 */
@Entity
public class Problem extends Model {

    public String spelling;
    public String unit_id;
    /**
     * <html> 题目
     */
    public String title;
    /**
     * <html> 题干，如果有图片或音频会直接以tag嵌入其中
     */
    public String body;
    /**
     * <int> 是关于哪个单词的（或者没有，即不关联任何单词）
     */
    public String word_composite_id;
    /**
     * <string> 复习还是练习（"practice"用于练习, "review"用于复习或挑战, "both"都行）
     */
    public String pool;
    /**
     * <string> 题型（“单选”, “多选”, “填空”, “图片”）
     */
    public String type;
    /**
     * 选择题的选项
     */
    @Embedded
    public List<ProblemChoice> choices;
    /**
     * <html> 题目解释（如果做错了会解释），可能没有
     */
    public String explanation;
    /**
     * <html> 提示，可能没有
     */
    public String hint;
    public String use;
    public String answer;
    public String audio;
    public String image;
    public String profieciency = "";
    public String wrong_select1;
    public String wrong_select2;
    public String wrong_select3;

    public String toString() {
        return word_composite_id;
    }

    public static class ProblemChoice {

        public String body;
        public boolean is_answer;

        public ProblemChoice(String body, boolean is_answer) {
            this.body = body;
            this.is_answer = is_answer;
        }
    }

}
