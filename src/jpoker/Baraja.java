/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class Baraja {
    private final List<Carta> cartas;
    private int index=0;

    public Baraja() {
        this.cartas = obtenerTodasCartas();
    }
    
    public Carta obtenerCarta(){
        Carta result = null;
        if (index < cartas.size()) {
            result = cartas.get(index);
            index++;
        }
        return result;
    }
    
    public void barajar(){
        index=0;
        Collections.shuffle(cartas);
    }
    
    public static List<Carta> obtenerTodasCartas(){
        int numCartas = Carta.Palo.values().length * Carta.Rango.values().length;
        List<Carta> result = new ArrayList<>(numCartas);
        for (Carta.Palo palo : Carta.Palo.values()) {
            for (Carta.Rango rango : Carta.Rango.values()) {
                result.add(new Carta(palo,rango));                
            }            
        }
        return result;
    }
    
}
