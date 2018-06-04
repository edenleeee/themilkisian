package com.example.leeso.themilkisian.Sample;

public class Comment {
    int point;
    String usercomment;

    public Comment() {
    }

    public Comment(int point, String usercomment) {
        this.point = point;
        this.usercomment = usercomment;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getUsercomment() {
        return usercomment;
    }

    public void setUsercomment(String usercomment) {
        this.usercomment = usercomment;
    }
}
