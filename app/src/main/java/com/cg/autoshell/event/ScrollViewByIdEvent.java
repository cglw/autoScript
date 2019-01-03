package com.cg.autoshell.event;

/**
 * Created by chengang on 2017/11/13.
 */

public class ScrollViewByIdEvent {
    public String id;
    public String dir = "";

    public ScrollViewByIdEvent(String id, String dir) {
        this.id = id;
        this.dir = dir;
    }
}
