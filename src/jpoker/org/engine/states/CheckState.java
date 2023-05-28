/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.engine.states;

import jpoker.org.engine.model.ModelContext;
import jpoker.org.engine.model.ModelUtil;
import jpoker.org.poker.api.game.TexasHoldEmUtil;
import static jpoker.org.poker.api.game.TexasHoldEmUtil.PlayerState.READY;
import jpoker.org.util.statemachine.IState;
import sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class CheckState implements IState<ModelContext>{
    public static final String NAME = "Next";
    private static final TexasHoldEmUtil.GameState[] GAME_STATE=TexasHoldEmUtil.GameState.values();
    private static final int[] OBTAIN_CARDS={3,1,1,0,0};
    
    public String getName(){
        return NAME;
    }
    
    private int indexByGameState(TexasHoldEmUtil.GameState gameState){
        int i=0;
        while (i<GAME_STATE.length && GAME_STATE[i] != gameState) {            
            i++;
        }
        return i;
    }
    
    public boolean execute(ModelContext model) {
        int indexGameState = indexByGameState(model.getGameState());
        if (OBTAIN_CARDS[indexGameState]>0) {
            model.addCommunityCards(OBTAIN_CARDS[indexGameState]);
        }
        model.setGameState(GAME_STATE[indexGameState+1]);
        model.setActivePlayers(model.getActivePlayers());
        model.setBets(0);
        model.getPlayers().stream().filter(p->p.isActive()).forEach(p->p.setState(READY));
        model.setPlayerTurn(ModelUtil.nextPlayer(model, model.getDealer()));
        model.setLastBetCommand(null);
        model.setLastPlayerBet(null);
        return true;
    }
}
