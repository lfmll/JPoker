/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.engine.controller;

import java.util.List;
import java.util.Random;
import jpoker.org.poker.api.core.Carta;
import jpoker.org.poker.api.game.BetCommand;
import jpoker.org.poker.api.game.GameInfo;
import jpoker.org.poker.api.game.IStrategy;
import jpoker.org.poker.api.game.PlayerInfo;
import jpoker.org.poker.api.game.TexasHoldEmUtil;
import jpoker.org.poker.api.game.TexasHoldEmUtil.BetCommandType;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class RandomStrategy implements IStrategy{
    private static final Random RAND=new Random();
    private final String name;
    private double aggresivity = 0.5 + RAND.nextDouble()/2;
    private double aggressivity;
    private BetCommand lastBet;
    
    public RandomStrategy(String name) {
        this.name = "Random-" + name;
    }
    
    @Override
    public String getName(){
        return name;
    }
    
    @Override
    public void updateState(GameInfo<PlayerInfo> state){
        lastBet=null;
    }
    
    @Override
    public String toString(){
        return String.join("{RandomStrategry-", name,"}");
    }
    
    @Override
    public void check(List<Carta> communityCards) {
        lastBet=null;
    }
    
    private long getMaxBet(GameInfo<PlayerInfo> state) {
        if (aggresivity > 1.d) {
            return Long.MAX_VALUE;
        }
        long players = state.getPlayers().stream().filter(p->p.isActive() || p.getState()==TexasHoldEmUtil.PlayerState.ALL_IN).count();
        double probability = 1.0D/players;
        long pot = state.getPlayers().stream().mapToLong(p->p.getBet()).sum();
        return Math.round((probability*pot/(1-probability)) * aggresivity);
    }
    
    @Override
    public BetCommand getCommand(GameInfo<PlayerInfo> state) {
        PlayerInfo ownInfo = state.getPlayer(state.getPlayerTurn());
        calcAggressivity(state, ownInfo);
        long otherPlayerMaxBet = state.getPlayers().stream().max((p0,p1)-> Long.compare(p0.getBet(), p1.getBet())).get().getBet();
        long minBet = Math.max(otherPlayerMaxBet - ownInfo.getBet(), state.getSettings().getBigBind());
        long maxBet = getMaxBet(state);
        long chips = ownInfo.getChips();
        BetCommand result;
        if (minBet > maxBet) {
            result = new BetCommand(TexasHoldEmUtil.BetCommandType.FOLD);
        } else if (maxBet >= chips) {
            result = new BetCommand(TexasHoldEmUtil.BetCommandType.ALL_IN);
        } else if (maxBet > minBet && (lastBet == null || lastBet.getType()!=BetCommandType.RAISE)) {
            result = new BetCommand(TexasHoldEmUtil.BetCommandType.RAISE, maxBet);
        } else if (otherPlayerMaxBet==state.getSettings().getBigBind()||minBet==0) {
            result = new BetCommand(TexasHoldEmUtil.BetCommandType.CHECK);
        } else {
            result = new BetCommand(BetCommandType.CALL);
        }
        lastBet = result;
        return result;
    }
    
    private void calcAggressivity(GameInfo<PlayerInfo> state, PlayerInfo player) {
        long allChips = state.getPlayers().stream().filter(p-> p.isActive() || p.getState() == TexasHoldEmUtil.PlayerState.ALL_IN).mapToLong(p -> p.getChips()).sum();
        long players = state.getPlayers().stream().filter(p -> p.isActive() || p.getState() == TexasHoldEmUtil.PlayerState.ALL_IN && p.getChips() > 0).count();
        long myChips = player.getChips();
        double proportion = (allChips - myChips)/players;
        aggresivity = (myChips /(proportion + myChips))/2 + 0.70d;
        if (myChips > (allChips - myChips)) {
            aggressivity = 1.1;
        }
    }
}
