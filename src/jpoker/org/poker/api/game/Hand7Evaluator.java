/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.poker.api.game;

import java.util.List;
import jpoker.org.poker.api.core.Carta;
import jpoker.org.poker.api.core.Combination;
import jpoker.org.poker.api.core.IManoEvaluador;
import static jpoker.org.poker.api.game.TexasHoldEmUtil.COMMUNITY_CARDS;
import static jpoker.org.poker.api.game.TexasHoldEmUtil.PLAYER_CARDS;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class Hand7Evaluator {
    public static final int TOTAL_CARDS = PLAYER_CARDS + COMMUNITY_CARDS;
    private final int[] combinatorialBuffer = new int[COMMUNITY_CARDS];
    private final Combination combinatorial = new Combination(COMMUNITY_CARDS, TOTAL_CARDS);
    private final IManoEvaluador evaluator;
    private final Carta[] evalBuffer = new Carta[COMMUNITY_CARDS];
    private final Carta[] cards = new Carta[TOTAL_CARDS];
    private int communityCardsValue=0;

    public Hand7Evaluator(IManoEvaluador evaluator) {
        this.evaluator = evaluator;
    }
    
    public void setCommunityCards(List<Carta> cc){
        int i=0;
        for (Carta card : cc) {
            evalBuffer[i] = card;
            cards[i++] = card;
        }
        communityCardsValue = evaluator.eval(evalBuffer);
    }
    
    public int eval(Carta c0, Carta c1){
        cards[COMMUNITY_CARDS] = c0;
        cards[COMMUNITY_CARDS+1] = c1;
        return evalCards();
    }
    
    static Carta[] copy(Carta[] src, Carta[] target, int[] positions){
        int i=0;
        for (int p : positions) {
            target[i++]=src[p];
        }
        return target;
    }
    
    private int evalCards(){
        combinatorial.clear();
        combinatorial.next(combinatorialBuffer);
        int result = communityCardsValue;
        while (combinatorial.hasNext()) {            
            result = Math.max(result, evaluator.eval(copy(cards, evalBuffer,combinatorial.next(combinatorialBuffer))));            
        }
        return result;
    }
}
