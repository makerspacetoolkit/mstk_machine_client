package com.nyxaria.apps.Controller;

import co.gongzh.procbridge.APIHandler;
import co.gongzh.procbridge.ProcBridgeServer;
import com.nyxaria.apps.Model.U;
import org.json.JSONObject;

import java.io.IOException;

public class RFIDReader {

    public static void init() {

        int port = 8877;
        long timeout = 10000; // 10 seconds

        ProcBridgeServer server = new ProcBridgeServer(port, timeout, new Object() {

            @APIHandler
            JSONObject echo(JSONObject arg) {
                System.out.println(arg);
                if(arg.has("uuid")) {
                    U.triggerCardRead(arg.get("uuid").toString());
                } else if(arg.has("job")) {
                    U.triggerFilterPinChanged(arg.get("job").equals("1") ? 1 : 0);
                } else if(arg.has("in_service")) {
                    U.triggerInService(arg.get("in_service").equals("1"));

                }
                return arg;
            }

        });

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
