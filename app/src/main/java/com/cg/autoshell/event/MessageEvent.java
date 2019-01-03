package com.cg.autoshell.event;

/**
 * Created by chengang on 2017/11/15.
 */

public class MessageEvent {
    public String msg;
    public String type;
    public MessageEvent(String msg,String type)
    {
        this.msg=msg;
        this.type=type;
    }
}
