/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.util.timer;

import jpoker.org.util.dispatcher.IGameEventDispatcher;

/**
 *
 * @author luisfernandomedinallorenti
 */
public interface IGameTimer extends Runnable{
    public void exit();
    public long getTime();
    public void resetTimer(Long timeroutId);
    public void setTime(long time);
    public IGameEventDispatcher getDispatcher();
    public void setDispatcher(IGameEventDispatcher dispatcher);
}
