package details;

import java.io.Serializable;

public class AlertDetail implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private long alertid;
    private String text;
    private String title;
    public AlertDetail() {
        super();
    }
    public AlertDetail(long alertid, String text, String title) {
        super();
        this.alertid = alertid;
        this.text = text;
        this.title = title;
    }
    public long getAlertid() {
        return alertid;
    }
    public void setAlertid(long alertid) {
        this.alertid = alertid;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object arg0) {
        return super.equals(arg0);
    }
    @Override
    public String toString() {
        return super.toString();
    }
}
