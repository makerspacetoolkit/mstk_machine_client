package com.nyxaria.apps.Controller;

import co.gongzh.procbridge.ProcBridge;
import co.gongzh.procbridge.ProcBridgeException;
import org.json.JSONObject;

public class GPIOHandler {

    public static final int HIGH = 1;
    public static final int LOW  = 0;

    private static ProcBridge pb;


    public static void init() {
        if(pb != null) return;

        String host = "127.0.0.1";
        int port = 7788;
        long timeout = 10000; // 10 seconds

        pb = new ProcBridge(host, port, timeout);
    }

    public static void writeInterlock(int state) {
        JSONObject resp;
        try {
            resp = pb.request("echo", "{pin:"+state+"}");
            System.out.println(resp); // prints "{}"
        } catch (ProcBridgeException e) {
            e.printStackTrace();
        }
    }

    public static void writeFilterAlarmState(int state) {
        JSONObject resp;
        try {
            resp = pb.request("echo", "{filter_alarm:"+state+"}");
            System.out.println(resp); // prints "{}"
        } catch (ProcBridgeException e) {
            e.printStackTrace();
        }
    }
}
