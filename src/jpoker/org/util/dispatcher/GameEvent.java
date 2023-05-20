/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.util.dispatcher;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class GameEvent {
    private String type;
    private String source;
    private Object payload;

    public GameEvent() {
    }

    public GameEvent(String type, String source) {
        this.type = type;
        this.source = source;
    }

    public GameEvent(String type, String source, Object payload) {
        this.type = type;
        this.source = source;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
        
}
