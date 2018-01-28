package com.nyxaria.apps;

import com.nyxaria.apps.Model.U;

public class Main {


    public Main() {
        U.init();
    }


    public static void main(String[] args) {
        if(args.length > 0) {
            if(args[0].equals("NG")) {
                U.debugging = true;
            }
        }
        new Main();

    }
}
