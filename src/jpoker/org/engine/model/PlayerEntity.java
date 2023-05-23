/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.engine.model;

import jpoker.org.poker.api.game.BetCommand;
import jpoker.org.poker.api.game.PlayerInfo;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class PlayerEntity extends PlayerInfo{
    private int handValue=0;
    private BetCommand betCommand;
    private boolean showCards;

    public PlayerEntity() {
    }
    
    public boolean showCards(){
        return showCards;
    }
    
    public void showCards(boolean showCards){
        this.showCards=showCards;
    }

    public int getHandValue() {
        return handValue;
    }

    public void setHandValue(int handValue) {
        this.handValue = handValue;
    }

    public BetCommand getBetCommand() {
        return betCommand;
    }

    public void setBetCommand(BetCommand betCommand) {
        this.betCommand = betCommand;
    }
        
}
