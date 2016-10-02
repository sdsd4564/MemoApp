package hanbat.encho.com.clipboardmake;

import android.content.ClipData;
import android.net.Uri;

import static android.R.attr.checked;

/**
 * Created by USER on 2016-09-10.
 */
public class Entity {
    public int _id = 0;
    public String memo = "";
    public boolean checked = false;
    public Uri image = null;

    public Entity(int _id, Uri image, boolean checked) {
        this._id = _id;
        this.image = image;
        this.checked = checked;
    }

    public Entity(int _id, String memo, boolean checked) {
        this._id = _id;
        this.memo = memo;
        this.checked = checked;
    }
}
