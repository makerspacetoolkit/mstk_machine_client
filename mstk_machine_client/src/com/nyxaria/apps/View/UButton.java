package com.nyxaria.apps.View;

import javax.swing.*;
import java.awt.*;

public class UButton extends JButton {
    public UButton(String s) {
        super(s);
        setOpaque(true);
        setBorderPainted(false);
        setBackground(new Color(11, 173, 252));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(150,80));
        setMinimumSize(new Dimension(150,80));
        setMaximumSize(new Dimension(150,80));

        setFont(new Font(getFont().getName(), Font.PLAIN, 22));

    }

}
