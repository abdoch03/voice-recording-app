package com.example.voice_memo;

public class VoiceMemo {
    private String name;
    private String path;
    private long duration;
    private String date;

    public VoiceMemo(String name, String path, long duration, String date) {
        this.name = name;
        this.path = path;
        this.duration = duration;
        this.date = date;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public String getDate() {
        return date;
    }

}
