/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.engine.states;

import com.sun.media.sound.ModelConnectionBlock;
import java.util.List;
import jpoker.org.engine.model.ModelContext;
import jpoker.org.engine.model.PlayerEntity;
import jpoker.org.poker.api.game.TexasHoldEmUtil;
import jpoker.org.util.statemachine.IState;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class WinnerState implements IState<ModelContext>{
    public static final String NAME="Winner";
    
    @Override
    public String getName(){
        return NAME;
    }
    
    @Override
    public boolean execute(ModelContext model){
        List<PlayerEntity> players = model.getPlayers();
        players.stream()
                .filter(p->p.isActive()|| p.getState() == TexasHoldEmUtil.PlayerState.ALL_IN)
                .findFirst()
                .get()
                .addChips(players
                    .stream()
                    .mapToLong(p->p.getBet())
                    .sum());
        return true;
    }
}
