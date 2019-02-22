package edu.whut.huangxun.HuangXunMusic;
import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Serializable{
    private String name;
    private String singername;
    private int coverId;
    private String songpath;


    public Song(String name, int imageId, String singername, String songpath) {
        this.name = name;
        this.coverId = imageId;
        this.singername = singername;
        this.songpath = songpath;
    }


    public String getName() {
        return name;
    }

    public String getSingerName() {
        return singername;
    }

    public int getImageId() {
        return coverId;
    }

    public String getsongPath() {
        return songpath;
    }


}

