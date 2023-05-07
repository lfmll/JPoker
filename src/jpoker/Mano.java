/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class Mano {
    public static final int CARTA=5;
    
    public enum Tipo{
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        STRAIGHT,
        FLUSH,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        STRAIGHT_FLUSH
    }
    
    private Mano(){
        
    }
}
