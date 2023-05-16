/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.util.statemachine;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class AfterStateDecorator<T> implements IState<T>{
    private final IState<T> state;
    private final Runnable listener;
    
    public AfterStateDecorator(IState<T> state, Runnable listener){
        this.state=state;
        this.listener=listener;
    }
    
    @Override
    public String getName(){
        return state.getName();
    }
    
    @Override
    public boolean execute(T context){
        boolean result=state.execute(context);
        if (result) {
            listener.run();
        }
        return result;
    }
}
