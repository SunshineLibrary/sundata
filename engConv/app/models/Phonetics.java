package models;

import com.google.code.morphia.annotations.Entity;
import play.modules.morphia.Model;

/**
 * User: fxp
 * Date: 13-8-17
 * Time: PM11:28
 */
@Entity
public class Phonetics extends Model {
    public String spelling;
    public int part;
    public String spelling_part;
    public String phonetic_part;
    public String audio_part;
    public String compositeId;

    public Phonetics(String spelling, int part) {
        this.spelling = spelling;
        this.part = part;
        this.compositeId = getCompositeId(spelling, part);
    }

    public static Phonetics createIfNotExists(String spelling, int part) {
        Phonetics ret = Phonetics.
                find("compositeId", getCompositeId(spelling, part))
                .first();
        if (ret == null) {
            ret = new Phonetics(spelling, part).save();
        }
        return ret;
    }

    public static String getCompositeId(String spelling, int part) {
        return spelling + ":" + part;
    }

    @Override
    public String toString() {
        return compositeId;
    }
}
