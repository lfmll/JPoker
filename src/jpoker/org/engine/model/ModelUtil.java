/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.engine.model;

import jpoker.org.poker.api.game.Settings;
import jpoker.org.poker.api.game.TexasHoldEmUtil;
import jpoker.org.poker.api.game.TexasHoldEmUtil.PlayerState;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class ModelUtil {
    public static final int NO_PLAYER_TURN=-1;

    public ModelUtil() {
    }
    
    public static boolean range(int min,int max,int value){
        return min <= value && value < max;
    }
    
    public static int nextPlayer(ModelContext model, int turn){
        int result=NO_PLAYER_TURN;
        int players = model.getNumPlayers();
        if (players > 1 && range(0, players, turn)) {
            int i=(turn +1) % players;
            while (i!= turn && result==NO_PLAYER_TURN) {
                if (model.getPlayer(i).isActive()) {
                    result = i;
                } else {
                    i = (i+1)%players;
                }                
            }
            result= checkNextPlayer(model,result);
        }
        return result;
    }
    
    private static int checkNextPlayer(ModelContext model, int index){
        int result=index;
        if (result != NO_PLAYER_TURN
                && model.getPlayer(result).getBet()==model.getHighBet()
                && (model.getPlayer(result).getState()!=PlayerState.READY
                || model.getActivePlayers()==1)) {
                
            result=NO_PLAYER_TURN;                        
        }
        return result;
    }
    
    public static void playerBet(ModelContext model, PlayerEntity player, TexasHoldEmUtil.BetCommandType betCommand, long chips){
        if (betCommand==TexasHoldEmUtil.BetCommandType.ALL_IN) {
            model.setPlayerAllIn(model.getPlayerAllIn()+1);
            model.setActivePlayers(model.getActivePlayers()-1);
        } else if (betCommand == TexasHoldEmUtil.BetCommandType.FOLD
                || betCommand == TexasHoldEmUtil.BetCommandType.TIMEOUT) {
            model.setActivePlayers(model.getActivePlayers()-1);            
        }
        playerBet(player,chips);
        model.setHighBet(Math.max(model.getHighBet(), player.getBet()));
        model.setBets(model.getBets()+1);
    }
    
    public static void playerBet(PlayerEntity player, long chips){
        player.setBet(player.getBet()+chips);
        player.setChips(player.getChips()-chips);
    }
    
    public static void invrementErrors(PlayerEntity player, Settings settings){
        int errors=player.getErrors()+1;
        player.setErrors(errors);
        if (errors >= settings.getMaxErrors()) {
            player.setState(PlayerState.OUT);
            player.setChips(0);
        } else {
            player.setState(PlayerState.FOLD);
        }
    }
}
