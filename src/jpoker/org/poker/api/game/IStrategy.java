/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.poker.api.game;

import java.util.List;
import jpoker.org.poker.api.core.Carta;

/**
 *
 * @author luisfernandomedinallorenti
 */
public interface IStrategy {
    public String getName();
    public BetCommand getCommand(GameInfo<PlayerInfo> state);
    public default void updateState(GameInfo<PlayerInfo> state){}
    public default void check(List<Carta> communityCards){}
    public default void onPlayerCommand(String player,BetCommand betCommand){}
}
