/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.poker.api.game;

import jpoker.org.engine.controller.GameException;

/**
 *
 * @author luisfernandomedinallorenti
 */
public interface IGameController {
    public void setSettings(Settings settings);
    public boolean addStrategy(IStrategy strategy);
    public void start() throws GameException;
    public void waitFinish();
}
