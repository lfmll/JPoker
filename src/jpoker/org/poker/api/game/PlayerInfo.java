/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.poker.api.game;

import jpoker.org.poker.api.core.Carta;
import jpoker.org.poker.api.game.TexasHoldEmUtil.PlayerState;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class PlayerInfo {
    private String name;
    private long chips;
    private long bet;
    private Carta[]cards=new Carta[TexasHoldEmUtil.PLAYER_CARDS];
    private PlayerState state;
    private int errors;

    public PlayerInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getChips() {
        return chips;
    }

    public void setChips(long chips) {
        this.chips = chips;
    }

    public long getBet() {
        return bet;
    }

    public void setBet(long bet) {
        this.bet = bet;
    }

    public Carta[] getCards() {
        return new Carta[]{cards[0],cards[1]};
    }
    
    public Carta getCard(int index){
        return cards[index];
    }

    public void setCards(Carta[] cards) {
        this.cards[0] = cards[0];
        this.cards[1] = cards[1];
    }

    public void setCards(Carta card0, Carta card1){
        this.cards[0]=card0;
        this.cards[1]=card1;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }
        
    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }
    
    public boolean isActive() {
        return state.isActive();
    }
    
    public void addChips(long chips){
        this.chips += chips;
    }
}
