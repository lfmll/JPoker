/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker;

import java.text.MessageFormat;

/**
 *
 * @author luisfernandomedinallorenti
 */
public final class Carta {
    
    public static enum Palo{
        ESPADA('♠'),
        CORAZON('♥'), 
        DIAMANTE('♦'), 
        TREBOL('♣');
        private Palo(char c){
            this.c=c;
        }
        private final char c;
    }
    
    public static enum Rango{
        DOS, TRES, CUATRO, CINCO, SEIS, SIETE, OCHO, NUEVE, JACK, REINA, REY, AS
    }
    
    private final Palo palo;
    private final Rango rango;
    
    private static final String STRING_RANK_CARDS = "23456789TJQKA";
    
    public Carta(Palo palo, Rango rango){                
        if (palo==null) {
            throw new IllegalArgumentException("palo no puede ser nulo");
        }
        if (rango==null) {
            throw new IllegalArgumentException("rango no puede ser nulo");
        }
        this.palo=palo;
        this.rango=rango;
    }

    public Palo getPalo() {
        return palo;
    }

    public Rango getRango() {
        return rango;
    }
    
    @Override
    public String toString(){
        int r = rango.ordinal();
        return STRING_RANK_CARDS.substring(r, r +1) + palo.c;
    }
    
    @Override
    public int hashCode(){
        return rango.ordinal()*Palo.values().length+palo.ordinal();
    }
    
    @Override
    public boolean equals(Object obj){
        boolean result=true;
        if (this!=obj) {
            result=false;
            if (obj!=null && getClass()==obj.getClass()) {
                result=hashCode()==((Carta)obj).hashCode();
            }
        }
        return result;
    }
    
}
