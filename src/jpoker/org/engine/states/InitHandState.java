/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.engine.states;

import java.util.List;
import jpoker.org.engine.model.ModelContext;
import jpoker.org.engine.model.ModelUtil;
import jpoker.org.engine.model.PlayerEntity;
import jpoker.org.poker.api.core.Baraja;
import jpoker.org.poker.api.game.Settings;
import jpoker.org.poker.api.game.TexasHoldEmUtil;
import static jpoker.org.poker.api.game.TexasHoldEmUtil.MIN_PLAYERS;
import jpoker.org.util.statemachine.IState;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class InitHandState implements IState<ModelContext> {
    public static final String NAME="InitHand";
    
    @Override
    public String getName(){
        return NAME;
    }
    
    @Override
    public boolean execute(ModelContext model){
        Baraja deck=model.getDeck();
        deck.barajar();
        Settings settings = model.getSettings();
        model.setGameState(TexasHoldEmUtil.GameState.PRE_FLOP);
        model.clearCommunityCard();
        model.setRound(model.getRound()+1);
        if (model.getRound() % settings.getRounds4IncrementBlind()==0) {
            settings.setSmallBind(2*settings.getSmallBind());
        }
        model.setPlayerAllIn(0);
        model.setHighBet(0L);
        List<PlayerEntity> players=model.getPlayers();
        
        for (PlayerEntity p : players) {
            p.setState(TexasHoldEmUtil.PlayerState.READY);
            p.setHandValue(0);
            p.setBet(0);
            p.showCards(false);
            p.setCards(deck.obtenerCarta(),deck.obtenerCarta());
        }
        int numPlayers = model.getNumPlayers();
        model.setActivePlayers(numPlayers);
        int dealerIndex = (model.getDealer()+1) % numPlayers;
        model.setDealer(dealerIndex);
        model.setPlayerTurn((dealerIndex+1) % numPlayers);
        if (numPlayers > MIN_PLAYERS) {
            compulsoryBet (model, settings.getSmallBind());
        }
        compulsoryBet(model,settings.getBigBind());
        return true;
    }
    
    private void compulsoryBet(ModelContext model, long chips) {
        int turn = model.getPlayerTurn();
        PlayerEntity player = model.getPlayer(turn);
        if (player.getChips() <= chips) {
            player.setState(TexasHoldEmUtil.PlayerState.ALL_IN);
            ModelUtil.playerBet(model, player, TexasHoldEmUtil.BetCommandType.ALL_IN, player.getChips());
        } else {
            ModelUtil.playerBet(player, chips);
        }
        model.setHighBet(chips);
        model.setPlayerTurn((turn+1) % model.getNumPlayers());
    }
}
