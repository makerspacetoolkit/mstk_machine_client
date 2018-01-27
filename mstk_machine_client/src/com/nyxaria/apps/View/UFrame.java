package com.nyxaria.apps.View;

import com.nyxaria.apps.Model.U;

import javax.swing.*;
import java.awt.*;

public class UFrame extends JFrame {

    public static final int WIDTH = 800, HEIGHT = 480;

    private static final U.Frame startFrame = U.Frame.Scan;

    public UFrame() {
        super();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocation(0, U.debugging ? 0 : 0); //left-top corner
        setUndecorated(true); //remove toolbar
        setResizable(false);

        getContentPane().setLayout(new BorderLayout());

        U.setCurrentFrame(startFrame);
        changeFrame(startFrame);

        setBackground(new Color(170,215,253));
        getContentPane().setBackground(new Color(170,215,253));

        setVisible(true);
        this.toFront();
    }

    public void changeFrame(U.Frame frame) {
        UPanel mainPane = new UPanel(frame);
        getContentPane().removeAll();
        mainPane.setOpaque(false);
        getContentPane().add(mainPane, BorderLayout.CENTER);
        getContentPane().doLayout();
        getContentPane().repaint();
        getContentPane().revalidate();

    }

}
