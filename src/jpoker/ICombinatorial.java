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
public interface ICombinatorial {
    public long combinations();
    public int size();
    public void clear();
    public int[] next(int[] items);
    public boolean hasNext();
}
