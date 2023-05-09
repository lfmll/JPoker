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
public class Combination implements ICombinatorial{
    private final int items;
    private final int[] indexes;
    
    public Combination(int subItems, int items){
        this.indexes=new int[subItems];
        this.items=items;
        init();
    }
    
    @Override
    public long combinations(){
        return combinations(indexes.length,items);
    }
    
    @Override
    public int size(){
        return indexes.length;
    }
    
    public int getSubIems(){
        return indexes.length;
    }
    
    public int getItems(){
        return items;
    }
        
    public boolean hasNext(int index){
        return indexes[index]+(indexes.length-index)<items;
    }
    
    private void move(int index){
        if (hasNext(index)) {
            indexes[index]++;
            int last=indexes[index];
            for (int i = index+1; i < indexes.length; i++) {
                this.indexes[i]= ++last;
            }
        } else {
            move(index-1);
        }
    }
    
    @Override
    public int[] next(int[] items){
        if (hasNext()) {
            move(indexes.length-1);
            System.arraycopy(indexes, 0, items, 0, indexes.length);
        }
        return items;
    }
    @Override
    public boolean hasNext(){
        return hasNext(0) || hasNext(indexes.length-1);
    }
    
    private void init(){
        int index = indexes.length;
        for (int i = 0; i < indexes.length; i++) {
            this.indexes[i]=i;
        }
        this.indexes[index-1]--;
    }
    
    @Override
    public void clear(){
        init();
    }
    
    public static long combinations(int subItems, int items){
        long result=1;
        int sub=Math.max(subItems, items-subItems);
        for (int i = sub + 1; i <= items; i++) {
            result = (result*i)/(i-sub);
        }
        return result;
    }
}
