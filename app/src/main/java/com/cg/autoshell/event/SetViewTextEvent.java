package com.cg.autoshell.event;

/**
 * Created by chengang on 2017/11/13.
 */

public class SetViewTextEvent {
    public String text;
    public String id;

    public SetViewTextEvent(String id,String text) {
        this.text = text;
        this.id = id;
    }
}
