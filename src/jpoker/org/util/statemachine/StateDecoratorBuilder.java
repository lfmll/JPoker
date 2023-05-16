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
public class StateDecoratorBuilder<T> {
    private IState<T> state;

    public StateDecoratorBuilder(IState<T> state) {
        this.state = state;
    }
    
    public static <T> StateDecoratorBuilder<T> create(IState<T> state){
        return new StateDecoratorBuilder<>(state);
    }
    
    public StateDecoratorBuilder<T> after(Runnable r){
        this.state = new AfterStateDecorator<>(state, r);
        return this;
    }
    
    public StateDecoratorBuilder<T> before(Runnable r){
        this.state = new BeforeStateDecorator<>(state, r);
        return this;
    }
    
    public IState<T> build(){
        return state;
    }
    
    public static <T> IState<T> after(IState<T> state, Runnable r){
        return new AfterStateDecorator<>(state, r);
    }
    
    public static <T> IState<T> before(IState<T> state, Runnable r){
        return new BeforeStateDecorator<>(state, r);
    }
}
