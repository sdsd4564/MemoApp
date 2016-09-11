package hanbat.encho.com.clipboardmake;

import android.content.ClipData;

/**
 * Created by USER on 2016-09-10.
 */
public class Entity {
    public int _id = 0;
    public String memo = "";
    public ClipData data = null;

    public Entity() {
    }

    public Entity(int _id, String memo) {
        this._id = _id;
        this.memo = memo;
    }

    public Entity(int _id, ClipData data) {
        this._id = _id;
        this.data = data;
    }
}
