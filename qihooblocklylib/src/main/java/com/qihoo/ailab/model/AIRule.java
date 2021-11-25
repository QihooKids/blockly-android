package com.qihoo.ailab.model;

public class AIRule {
    private String id;
    private String icon;
    private String des;
    private int open;
    private String title;

    public static AIRule test(){
        AIRule rule = new AIRule();
        rule.setId("test");
        rule.setTitle("人形检测");
        return rule;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "AIRule{" +
                "id='" + id + '\'' +
                ", icon='" + icon + '\'' +
                ", des='" + des + '\'' +
                ", open=" + open +
                ", title='" + title + '\'' +
                '}';
    }
}
