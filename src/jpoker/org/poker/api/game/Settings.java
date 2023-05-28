/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.poker.api.game;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class Settings {
    private int maxPlayers;
    private long time;
    private int maxErrors;
    private int maxRounds;
    private long playerChip;
    private long smallBind;
    private int rounds4IncrementBlind;

    public Settings() {
    }
    
    public Settings(Settings s){
        this.maxPlayers=s.maxPlayers;
        this.time=s.time;
        this.maxErrors=s.maxErrors;
        this.playerChip=s.playerChip;
        this.smallBind=s.smallBind;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public long getTime() {
        return time;
    }

    public int getMaxErrors() {
        return maxErrors;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public long getPlayerChip() {
        return playerChip;
    }

    public long getSmallBind() {
        return smallBind;
    }

    public long getBigBind() {
        return smallBind*2;
    }
    public int getRounds4IncrementBlind() {
        return rounds4IncrementBlind;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setMaxErrors(int maxErrors) {
        this.maxErrors = maxErrors;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    public void setPlayerChip(long playerChip) {
        this.playerChip = playerChip;
    }

    public void setSmallBind(long smallBind) {
        this.smallBind = smallBind;
    }

    public void setRounds4IncrementBlind(int rounds4IncrementBlind) {
        this.rounds4IncrementBlind = rounds4IncrementBlind;
    }
    
    
}
