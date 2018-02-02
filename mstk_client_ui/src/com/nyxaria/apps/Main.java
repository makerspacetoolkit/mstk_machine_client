package com.nyxaria.apps;

import com.nyxaria.apps.Model.ConnectionManager;
import com.nyxaria.apps.Model.U;

public class Main {


    public Main() {
        U.init();
    }


    public static void main(String[] args) {
        if(args.length > 0) {
            String[] tokens = args[0].split("=");
            System.out.println(tokens[1]);
            if(tokens[0].equals("apikey")) {
                ConnectionManager.apikey = tokens[1];
            }
            if(args.length > 1) {
               if(args[1].equals("NG")) {
                   System.out.println(args[1]);
                   U.debugging = true;
               }
            }
        }
        new Main();

    }
}
