/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashSet;
import java.util.Set;
import jpoker.Carta;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class NewEmptyJUnitTest {
    
    private static Carta[] getAllCards(){
        Carta[] result= new Carta[Carta.Palo.values().length * Carta.Rango.values().length];
        int i=0;
        for (Carta.Palo palo : Carta.Palo.values()) {
            for (Carta.Rango rango : Carta.Rango.values()) {
                Carta c = new Carta(palo, rango);
                result[i] = c;
                i++;                
            }
        }
        return result;
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void testHasCode(){
        System.out.println("hasCode");
        Carta[] allCards=getAllCards();
        Set<Integer> hashCodes = new HashSet<>(allCards.length);
        for (Carta card : allCards) {
            assertThat(hashCodes, not(hasItem(card.hashCode())));
        }
    }
    
    @Test
    public void testEqualsOtherObjects(){
        System.out.println("equalOtherObjects");
        Carta card = new Carta(Carta.Palo.ESPADA, Carta.Rango.CINCO);
        assertNotEquals("Carta: "+card+" !=null", card, null);
        assertNotEquals("Carta: "+card+" !=0", card, 0);
        assertNotEquals("Carta: "+card+" !=\"5E\"", card, "5E");        
        
    }
    
    @Test
    public void testEquals(){
        System.out.println("equals");
        int i=0;
        for (Carta card0 : getAllCards()) {
            int j=0;
            for (Carta card1 : getAllCards()) {
                if (i==j) {
                    assertEquals(card0, card1);
                }
                j++;                
            }
            i++;
        }
    }
    
    @Test
    public void testEqualsDistinct(){
        System.out.println("equal distinct");
        int i=0;
        for (Carta card0 : getAllCards()) {
            int j=0;
            for (Carta card1 : getAllCards()) {
                if (i!=j) {
                    assertNotEquals(card0, card1);
                }
                j++;                
            }
            i++;
        }
    }
}
