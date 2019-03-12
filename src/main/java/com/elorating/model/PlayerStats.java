package com.elorating.model;

import java.util.Date;

public class PlayerStats {

    protected int won;
    protected int lost;
    protected int draw;
    protected Date lastMatchDate;

    public PlayerStats() {
        this.won = 0;
        this.lost = 0;
        this.draw = 0;
    }

    public int getWon() {
        return won;
    }

    public int getLost() {
        return lost;
    }

    public int getDraw() {
        return draw;
    }

    public void addWon() {
        won++;
    }

    public void addLost() {
        lost++;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public void addDraw() {
        draw++;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public Date getLastMatchDate() {
        return lastMatchDate;
    }

    public void setLastMatchDate(Date lastMatchDate) {
        this.lastMatchDate = lastMatchDate;
    }
}
