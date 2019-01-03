package com.cg.autoshell.event;

/**
 * Created by chengang on 2017/11/13.
 */

public class ScrollViewByClassNameEvent {
    public String className;
    public String dir = "";

    public ScrollViewByClassNameEvent(String className, String dir) {
        this.className = className;
        this.dir = dir;
    }
}
