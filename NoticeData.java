package lakshya.org.collegeadmin;

public class NoticeData {
    String title,image,date,time,key;

    public NoticeData() {
    }

    public NoticeData(String date,String time,String key,String title,String image) {
        this.date = date;
        this.time=time;
        this.key=key;
        this.title=title;
        this.image=image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
