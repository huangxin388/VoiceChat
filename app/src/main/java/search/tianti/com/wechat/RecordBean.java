package search.tianti.com.wechat;

public class RecordBean {

    private float time;
    private String path;

    public RecordBean(float time, String path) {
        this.time = time;
        this.path = path;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
