/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.engine.controller;

import jpoker.org.poker.api.game.BetCommand;
import jpoker.org.poker.api.game.GameInfo;
import jpoker.org.poker.api.game.IStrategy;
import jpoker.org.poker.api.game.PlayerInfo;
import jpoker.org.poker.api.game.TexasHoldEmUtil;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class AggressiveStrategy implements IStrategy {
    private final String name;
    
    public AggressiveStrategy(String name) {
        this.name=name;
    }
    
    @Override
    public String getName(){
        return name;
    }
    
    @Override
    public BetCommand getCommand(GameInfo<PlayerInfo> state) {
        return new BetCommand(TexasHoldEmUtil.BetCommandType.ALL_IN);
    }
}
