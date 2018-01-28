package com.nyxaria.apps.View;

import javax.swing.*;
import java.awt.*;

public class ULabel extends JLabel {

    public ULabel(String s, int fsize) {
        super(s);
        setFont(new Font(getFont().getName(), Font.PLAIN, fsize));
    }

    public ULabel(String s) {
        this(s, 20);
    }
}
