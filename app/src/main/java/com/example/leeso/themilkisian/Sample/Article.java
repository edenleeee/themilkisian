package com.example.leeso.themilkisian.Sample;

public class Article {
    String tag;
    String name;

    public Article() {
    }

    public Article(String tag, String name) {
        this.tag = tag;
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Article{" +
                "tag='" + tag + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
