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
public class Transition<T> {
    private final IState<T> origin;
    private final IState<T> target;
    private final IChecker<T> checker;
    
    public Transition(IState<T> origin, IState<T> target, IChecker<T> checker){
        this.origin=origin;
        this.target=target;
        this.checker=checker;
    }

    public IState<T> getOrigin() {
        return origin;
    }

    public IState<T> getTarget() {
        return target;
    }

    public IChecker<T> getChecker() {
        return checker;
    }
        
}
