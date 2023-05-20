/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.util.timer;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import jpoker.org.util.dispatcher.GameEvent;
import jpoker.org.util.dispatcher.IGameEventDispatcher;
import org.slf4j.LoggerFactory;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class GameTimer implements IGameTimer{
    private static final org.slf4j.Logger LOGGER=LoggerFactory.getLogger(GameTimer.class);
    public static final String TIMEOUT_EVENT_TYPE="timeOutCommand";
    
    private final String source;
    private long time;
    private IGameEventDispatcher dispatcher;
    private boolean reset=false;
    private volatile boolean exit=false;
    private final ExecutorService executors;
    private Long timeoutId;
    
    private String player;
           
    public GameTimer(String source, ExecutorService executors){
        this.source=source;
        this.executors=executors;
    }

    @Override
    public synchronized void exit() {
        this.exit=true;
        this.reset=false;
        this.player=null;
        notify();
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public void resetTimer(Long timeroutId) {
        this.timeoutId=timeoutId;
        this.reset=true;
        notify();
    }

    @Override
    public void setTime(long time) {
        this.time=time;
    }

    @Override
    public synchronized IGameEventDispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    public synchronized void setDispatcher(IGameEventDispatcher dispatcher) {
        this.dispatcher=dispatcher;
    }

    @Override
    public void run() {
        LOGGER.debug("run");
        while (!exit) {            
            try {
                doTask();
            } catch (Exception e) {
                LOGGER.error("Timer interrupted",e);
            }
        }
        LOGGER.debug("finish");
    }
    
    private synchronized void doTask() throws InterruptedException{        
        if (timeoutId==null) {
            wait();
        }
        if (timeoutId!=null) {
            reset=false;
            wait(time);
            if (!reset && timeoutId !=null) {
                GameEvent event=new GameEvent(TIMEOUT_EVENT_TYPE, source, player);
                executors.execute(new Runnable() {
                    @Override
                    public void run() {
                        dispatcher.dispatch(event);
                    }
                });
            }
        }
    }
}
