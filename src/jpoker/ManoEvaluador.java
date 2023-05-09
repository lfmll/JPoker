/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import jpoker.Mano.Tipo;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class ManoEvaluador implements IManoEvaluador {
    private static final int ENCODE_BASE=Carta.Rango.AS.ordinal()+1;
    private static final int INDEXES_LENGTH=2;
    private static final int RANK_INDEX=0;
    private static final int REPEATS_INDEX=1;
    private static final Tipo[][] MATRIX_TYPES ={
        {Tipo.HIGH_CARD},
        {Tipo.ONE_PAIR},
        {Tipo.TWO_PAIR},
        {Tipo.THREE_OF_A_KIND},
        {Tipo.FULL_HOUSE},
        {Tipo.FOUR_OF_A_KIND}
    };
    private final int[][] indexes=new int[Mano.CARTA][INDEXES_LENGTH];
    private final int[] rangos=new int[ENCODE_BASE];
    private final int[] palos=new int[Carta.Palo.values().length];
    private boolean isStraight=false;
    private boolean isFlush=false;
    
    @Override
    public int eval(Carta[] cartas) {
        ExceptionUtil.checkArrayLengthArgument(cartas, "cartas", Mano.CARTA);
        isFlush=false;
        Arrays.fill(palos, 0);
        Arrays.fill(rangos, 0);
        int index=0;
        Set<Carta> previousCards=new HashSet<>(Mano.CARTA);
        for (Carta carta : cartas) {
            ExceptionUtil.checkNullArgument(carta, "carta["+(index++)+"]");
            ExceptionUtil.checkArgument(previousCards.contains(carta), "La carta {} está repetida", cartas);
            previousCards.add(carta);
            rangos[carta.getRango().ordinal()]++;
            palos[carta.getPalo().ordinal()]++;
        }
        isFlush = palos[cartas[0].getPalo().ordinal()] == Mano.CARTA;
        isStraight = false;
        int straightCounter = 0;
        int j=0;
        for (int i = rangos.length-1; i >= 0; i--) {
            if (rangos[i]>0) {
                straightCounter++;
                isStraight=straightCounter == Mano.CARTA;
                indexes[j][RANK_INDEX]=i;
                indexes[j][REPEATS_INDEX]=rangos[i];
                upIndex(j++);
            } else {
                straightCounter = 0;
            }            
        }
        isStraight = isStraight || checkStraight5toAce (straightCounter);
        return calculateHandValue();
    }
    //Actualiza el orden de los pares en los indices
    private void upIndex(int i){
        int k=i;
        while (k>0 && indexes[k-1][REPEATS_INDEX]<indexes[k][REPEATS_INDEX]) {            
            int[] temp = indexes[k-1];
            indexes[k-1] = indexes[k];
            indexes[k] = temp;
            k--;
        }
    }
    private boolean checkStraight5toAce(int straightCntr){
        boolean straight5toAce = false;
        //Evalua si se trato del caso especial
        if (rangos[Carta.Rango.AS.ordinal()]==1 && straightCntr==Mano.CARTA-1) {
            //Si es el caso especial hay que reorganizar los indices
            straight5toAce=true;
            for (int i = 1; i < indexes.length; i++) {
                indexes[i-1][RANK_INDEX]=indexes[i][RANK_INDEX];                                
            }
            indexes[indexes.length-1][RANK_INDEX]=Carta.Rango.AS.ordinal();
        }
        return straight5toAce;
    }
    
    private int calculateHandValue(){
        Tipo tipo;
        if (isStraight) {
            tipo=isFlush ? Tipo.STRAIGHT_FLUSH : Tipo.STRAIGHT;
        } else if (isFlush) {
            tipo=Tipo.FLUSH;
        } else {
            tipo=MATRIX_TYPES[indexes[0][REPEATS_INDEX]-1][indexes[1][REPEATS_INDEX]-1];
        }
        return encodeValue(tipo,indexes);
    }
    
    private static int encodeValue(Tipo tipo, int[][]indexes){
        int result=tipo.ordinal();
        int i=0;
        int j=0;
        while (j<Mano.CARTA) {            
            for (int k = 0; k < indexes[i][REPEATS_INDEX]; k++) {
                result=result*ENCODE_BASE+indexes[i][RANK_INDEX];
                j++;
            }
            i++;
        }
        return result;
    }
}
