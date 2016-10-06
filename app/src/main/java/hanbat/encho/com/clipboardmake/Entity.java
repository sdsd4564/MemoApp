package hanbat.encho.com.clipboardmake;

/**
 * Created by USER on 2016-09-10.
 */
public class Entity {
    public int _id = 0;
    public String memo = "";
    public boolean checked = false;
    public int marked = 0;

    public Entity(int _id, String memo, boolean checked, int marked) {
        this._id = _id;
        this.memo = memo;
        this.checked = checked;
        this.marked = marked;
    }
}
