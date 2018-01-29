package com.nyxaria.apps.View;

import com.nyxaria.apps.Model.U;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class UPanel extends JPanel {

    public static final int largeFont = 40, mediumFont = 33, smallFont = 26;


    U.Frame currentFrame;
    public UPanel(U.Frame currentFrame) {
        super();
        System.out.println("Changing frame to: "+currentFrame);
        this.currentFrame = currentFrame;
        switch(currentFrame) {
            case Scan:
                setupScan();
                break;
            case Welcome:
                setupWelcome();
                break;
            case JobStarting:
                setupJobStarting();
                break;
            case JobInProgress:
                setupJobInProgress();
                break;
            case JobComplete:
                setupJobComplete();
                break;
            case History:
                setupHistory();
                break;
            case Error:
                setupError();
                break;
            case ConfirmSignout:
                setupConfirmSignout();
                break;
            case Goodbye:
                setupGoodbye();
                break;
            case Maintainance:
                setupMaintainance();
                break;
        }

        turnTransparent(getComponents());
    }


    private void turnTransparent(Component[] components) {
        for(Component component : components) {
            if(component instanceof JPanel) {
                ((JPanel) component).setOpaque(false);
                turnTransparent(((JPanel)component).getComponents());
            }
        }
    }

    private void setupScan() {
        setLayout(new GridBagLayout()); //centered

        JPanel wrapPane = new JPanel(new BorderLayout());

        ULabel textLabel = new ULabel("Scan your card here", largeFont);
        textLabel.setBorder(BorderFactory.createEmptyBorder(0,0,30,0));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);

        BufferedImage arrowImg = null;
        try {
            arrowImg = ImageIO.read(getClass().getResourceAsStream("/res/arrow_left.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image arrowImgScaled = arrowImg.getScaledInstance(500,100, Image.SCALE_SMOOTH);

        ImageIcon arrowIcon = new ImageIcon(arrowImgScaled);
        JLabel arrowLabel = new JLabel(arrowIcon);

        wrapPane.add(textLabel, BorderLayout.NORTH);
        wrapPane.add(arrowLabel, BorderLayout.SOUTH);

        add(wrapPane);
    }

    private void setupWelcome() {
        setLayout(new BorderLayout());

        //title
        ULabel titleLabel = new ULabel("Welcome, " + U.getName() + "!", largeFont);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40,0,40,0));
        JPanel titleWrap = new JPanel(new GridBagLayout());
        titleWrap.add(titleLabel);
        titleWrap.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        //content
        JPanel contentPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        ULabel memberCreditLabel = new ULabel("Member Credit: " + U.getMemberCredit(), mediumFont);
        memberCreditLabel.setBorder(BorderFactory.createEmptyBorder(50,0,0,0));

        ULabel machineCreditLabel = new ULabel("Machine Credit: " + U.getMachineCredit(), mediumFont);
        ULabel totalLabel = new ULabel("Total: " + U.getTotal(), mediumFont);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(memberCreditLabel);
        contentPane.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(machineCreditLabel);
        contentPane.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(totalLabel);
        contentPane.add(panel);

        ULabel availableMachineTime = new ULabel("Available " + U.getMachine() + " time: " + U.getTimeAvailable(), smallFont);
        availableMachineTime.setBorder(BorderFactory.createEmptyBorder(100,0,0,0));

        availableMachineTime.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPane.add(availableMachineTime);

        JPanel buttonPane = new JPanel(new BorderLayout());

        UButton historyButton = new UButton("History");
        historyButton.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));
        UButton signoutButton = new UButton("Sign out");
        signoutButton.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));

        historyButton.addActionListener(e -> U.triggerHistory());
        signoutButton.addActionListener(e -> U.triggerSignout());

        JPanel buttonWrap = new JPanel();
        buttonWrap.setLayout(new BoxLayout(buttonWrap, BoxLayout.Y_AXIS));

        buttonWrap.add(historyButton);
        buttonWrap.add(new Box.Filler(new Dimension(10,10),new Dimension(10,10),new Dimension(10,10)));
        buttonWrap.add(signoutButton);
        buttonWrap.add(new Box.Filler(new Dimension(10,10),new Dimension(10,10),new Dimension(10,10)));
        buttonWrap.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
        buttonPane.add(buttonWrap, BorderLayout.SOUTH);

        contentPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,50));

        add(titleWrap, BorderLayout.NORTH);
        add(contentPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.EAST);
    }


    private void setupError() {
        setLayout(new BorderLayout());

        //title
        ULabel titleLabel = new ULabel("Hello " + U.getName() + "!", largeFont);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40,0,40,0));
        JPanel titleWrap = new JPanel(new GridBagLayout());
        titleWrap.add(titleLabel);
        titleWrap.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        //content
        JPanel contentWrap = new JPanel();
        contentWrap.setPreferredSize(new Dimension(UFrame.WIDTH-200, (int) contentWrap.getPreferredSize().getHeight()));

        ULabel contentLabel = new ULabel("<html>"+U.getErrorMessage(), mediumFont-5);
        contentLabel.setPreferredSize(new Dimension(UFrame.WIDTH-200, 200));
        contentWrap.add(contentLabel);

        //trailing
        JPanel trailingWrap = new JPanel(new GridBagLayout());

        ULabel trailingLabel = new ULabel("meetup.com/fat-cat-fab-lab", smallFont);
        trailingLabel.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
        trailingWrap.add(trailingLabel);

        //finish
        add(titleWrap, BorderLayout.NORTH);
        add(contentWrap, BorderLayout.CENTER);
        add(trailingWrap, BorderLayout.SOUTH);
    }


    private void setupJobStarting() {
        setLayout(new BorderLayout());

        //title
        ULabel titleLabel = new ULabel(U.getName(), largeFont);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40,0,40,0));
        JPanel titleWrap = new JPanel(new GridBagLayout());
        titleWrap.add(titleLabel);
        titleWrap.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        //content
        JPanel contentWrap = new JPanel(new GridBagLayout());
        contentWrap.setPreferredSize(new Dimension(UFrame.WIDTH-200, (int) contentWrap.getPreferredSize().getHeight()));

        ULabel contentLabel = new ULabel("<html><center>Job starting...<br>It’s your responsibility to make sure you have enough credits to complete your job!!!</center></html>", mediumFont);
        contentLabel.setPreferredSize(new Dimension(UFrame.WIDTH-100, (int) 150));
        contentWrap.add(contentLabel);

        //trailing
        JPanel trailingWrap = new JPanel(new BorderLayout());

        UButton signoutButton = new UButton("Sign out");
        trailingWrap.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));
        signoutButton.addActionListener(e -> U.triggerSignout());

        trailingWrap.add(signoutButton, BorderLayout.EAST);

        //finish
        add(titleWrap, BorderLayout.NORTH);
        add(contentWrap, BorderLayout.CENTER);
        add(trailingWrap, BorderLayout.SOUTH);
    }

    private void setupJobInProgress() {
        setLayout(new BorderLayout());

        //title
        ULabel titleLabel = new ULabel(U.getName(), largeFont);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40,0,40,0));
        JPanel titleWrap = new JPanel(new GridBagLayout());
        titleWrap.add(titleLabel);
        titleWrap.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        //content
        JPanel contentWrap = new JPanel(new GridBagLayout());
        contentWrap.setPreferredSize(new Dimension(UFrame.WIDTH-200, (int) contentWrap.getPreferredSize().getHeight()));

        ULabel contentLabel = new ULabel("<html>Job In Progress<br>Elapsed time: 0s<br>Time remaining: " + U.getTimeRemaining(), mediumFont);
        contentLabel.setBorder(BorderFactory.createEmptyBorder(0,75,0,0));
        U.updateLabel("progress", contentLabel);

        contentWrap.add(contentLabel);

        //trailing
        JPanel trailingWrap = new JPanel(new BorderLayout());

        UButton signoutButton = new UButton("Sign out");
        signoutButton.addActionListener(e -> U.triggerSignout());

        BufferedImage sharkImg = null;
        try {
            sharkImg = ImageIO.read(getClass().getResourceAsStream("/res/shark_laser.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image sharkImgScaled = sharkImg.getScaledInstance(200,130,
                Image.SCALE_SMOOTH);

        JLabel sharkLabel = new JLabel(new ImageIcon(sharkImgScaled));
        sharkLabel.setBorder(BorderFactory.createEmptyBorder(0,10,10,0));

        JPanel signoutWrap = new JPanel(new BorderLayout());
        signoutWrap.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));
        signoutWrap.add(signoutButton, BorderLayout.SOUTH);
        trailingWrap.add(signoutWrap, BorderLayout.EAST);
        trailingWrap.add(sharkLabel, BorderLayout.CENTER);

        //finish
        add(titleWrap, BorderLayout.NORTH);
        add(contentWrap, BorderLayout.CENTER);
        add(trailingWrap, BorderLayout.SOUTH);
    }


    private void setupMaintainance() {
        setLayout(new BorderLayout());

        //content
        JPanel contentWrap = new JPanel(new GridBagLayout());

        ULabel contentLabel = new ULabel("Sorry, the " + U.getMachine() + " is down for maintenance.", mediumFont);
        contentLabel.setBorder(BorderFactory.createEmptyBorder(0,0,40,0));
        contentWrap.add(contentLabel);

        //trailing
        JPanel trailingWrap = new JPanel(new BorderLayout());


        BufferedImage pandaImg = null;
        try {
            pandaImg = ImageIO.read(getClass().getResourceAsStream("/res/sad-panda.jpeg"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        Image pandaImgScaled = pandaImg.getScaledInstance(400,280,
                Image.SCALE_SMOOTH);

        ImageIcon pandaIcon = new ImageIcon(pandaImgScaled);
        JLabel pandaLabel = new JLabel(pandaIcon);
        pandaLabel.setBorder(BorderFactory.createEmptyBorder(0,10,10,0));

        trailingWrap.add(pandaLabel, BorderLayout.CENTER);

        //finish
        add(contentWrap, BorderLayout.SOUTH);
        add(trailingWrap, BorderLayout.CENTER);
    }

    private void setupJobComplete() {
        setLayout(new BorderLayout());
        //title
        ULabel titleLabel = new ULabel(U.getName(), largeFont);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40,0,40,0));
        JPanel titleWrap = new JPanel(new GridBagLayout());
        titleWrap.add(titleLabel);
        titleWrap.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        //content
        JPanel contentWrap = new JPanel(new GridBagLayout());
        contentWrap.setPreferredSize(new Dimension(UFrame.WIDTH-200, (int) contentWrap.getPreferredSize().getHeight()));

        ULabel contentLabel = new ULabel("Job " + U.getJobID() + " complete: " + U.getMachineTime() + " / " + U.getFinalCost(), mediumFont);

        contentWrap.add(contentLabel);

        //trailing
        JPanel trailingWrap = new JPanel(new BorderLayout());

        UButton signoutButton = new UButton("Sign out");
        JPanel signoutWrap = new JPanel(new BorderLayout());
        signoutWrap.add(signoutButton, BorderLayout.SOUTH);
        signoutWrap.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));

        signoutButton.addActionListener(e -> U.triggerSignout());
        JPanel detailsWrap = new JPanel(new BorderLayout());

        ULabel newBalanceLabel = new ULabel("New balance: " + U.getNewBalance(), mediumFont);
        ULabel newTimeRemaining = new ULabel("New time remaining: " + U.getTimeAvailable(), mediumFont);

        detailsWrap.add(newBalanceLabel, BorderLayout.NORTH);
        detailsWrap.add(newTimeRemaining, BorderLayout.SOUTH);

        detailsWrap.setBorder(BorderFactory.createEmptyBorder(0,50,20,0));

        trailingWrap.add(signoutWrap, BorderLayout.EAST);
        trailingWrap.add(detailsWrap, BorderLayout.CENTER);

        //finish
        add(titleWrap, BorderLayout.NORTH);
        add(contentWrap, BorderLayout.CENTER);
        add(trailingWrap, BorderLayout.SOUTH);
    }

    private void setupConfirmSignout() {
        setLayout(new BorderLayout());

        //title
        ULabel titleLabel = new ULabel(U.getName(), largeFont);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40,0,40,0));
        JPanel titleWrap = new JPanel(new GridBagLayout());
        titleWrap.add(titleLabel);
        titleWrap.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        //content
        JPanel contentWrap = new JPanel(new GridBagLayout());
        contentWrap.setPreferredSize(new Dimension(UFrame.WIDTH-200, (int) contentWrap.getPreferredSize().getHeight()));

        ULabel contentLabel = new ULabel("<html><center>There is a job in progress.<br>Do you want to stop it and sign out?", smallFont+5);

        contentWrap.add(contentLabel);

        //trailing
        JPanel trailingWrap = new JPanel(new GridBagLayout());

        UButton yesButton = new UButton("yes");
        UButton noButton = new UButton("no");

        yesButton.addActionListener(e -> U.finaliseSignout());
        noButton.addActionListener(e -> U.stopSignout());

        JPanel buttonWrap = new JPanel();
        buttonWrap.setLayout(new BoxLayout(buttonWrap, BoxLayout.X_AXIS));

        buttonWrap.add(yesButton);
        int x = 100;
        buttonWrap.add(new Box.Filler(new Dimension(x,10),new Dimension(x,10),new Dimension(x,10)));
        buttonWrap.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
        buttonWrap.add(noButton);

        trailingWrap.add(buttonWrap);

        //finish
        add(titleWrap, BorderLayout.NORTH);
        add(contentWrap, BorderLayout.CENTER);
        add(trailingWrap, BorderLayout.SOUTH);
    }

    private void setupGoodbye() {
        setLayout(new BorderLayout());

        //title
        ULabel titleLabel = new ULabel("Thank you, " + U.getName() + "!", largeFont);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40,0,40,0));
        JPanel titleWrap = new JPanel(new GridBagLayout());
        titleWrap.add(titleLabel);
        titleWrap.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        //content
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        ULabel nextTimeLabel = new ULabel("See you next time!", mediumFont + 5);
        nextTimeLabel.setBorder(BorderFactory.createEmptyBorder(30,0,20,40));

        ULabel memberCreditLabel = new ULabel("Member Credit: " + U.getMemberCredit(), mediumFont);
        ULabel machineCreditLabel = new ULabel("Machine Credit: " + U.getMachineCredit(), mediumFont);
        ULabel totalLabel = new ULabel("Total: " + U.getTotal(), mediumFont);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(nextTimeLabel);
        nextTimeLabel.setBorder(BorderFactory.createEmptyBorder(20,0,30,20));
        contentPane.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(memberCreditLabel);
        contentPane.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(machineCreditLabel);
        contentPane.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(totalLabel);
        contentPane.add(panel);


        ULabel availableMachineTime = new ULabel("Available " + U.getMachine() + " time: " + U.getTimeLeft(), mediumFont);
        availableMachineTime.setBorder(BorderFactory.createEmptyBorder(60,0,0,0));
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(availableMachineTime);
        contentPane.add(panel);

        add(titleWrap, BorderLayout.NORTH);
        add(contentPane, BorderLayout.CENTER);
        contentPane.setBorder(BorderFactory.createEmptyBorder(0, 0,0,200));
    }

    private void setupHistory() {
        setLayout(new BorderLayout());
        //content

        ULabel textArea = new ULabel(U.getHistory(), 14);
        textArea.setBorder(BorderFactory.createEmptyBorder(0,10, 0, 0));

        //button stuff

        JPanel buttonWrap = new JPanel(new BorderLayout());
        UButton backButton = new UButton("back");
        UButton previousButton = new UButton("previous");
        UButton nextButton = new UButton("next");
        previousButton.setVisible(U.getPageIndex() != 0); //first page on startup
        nextButton.setEnabled(!U.isLastPage()); //first page on startup
        ULabel pageIndexLabel = new ULabel("Page " +(1+U.getPageIndex()),18);
        pageIndexLabel.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));

        previousButton.addActionListener(e -> {
            if(U.isLoading()) return;
            U.previousPage();
            textArea.setText(U.getHistory());
            if(U.getPageIndex() == 0) {
                previousButton.setVisible(false);
            }
            if(!U.isLastPage()) {
                nextButton.setEnabled(true);
            }
            pageIndexLabel.setText("Page "+(U.getPageIndex()+1));
        });
        nextButton.addActionListener(e -> {
            if(U.isLoading()) return;
                    U.nextPage();
            textArea.setText(U.getHistory());
            if(U.getPageIndex() != 0) {
                previousButton.setVisible(true);
            }
            if(U.isLastPage()) {
                nextButton.setEnabled(false);
            }
            pageIndexLabel.setText("Page "+(U.getPageIndex()+1));
        });
        backButton.addActionListener(e -> U.goBack());


        JPanel controlsWrap = new JPanel(new BorderLayout());

        controlsWrap.add(nextButton, BorderLayout.SOUTH);
        JLabel filler = new JLabel();
        filler.setPreferredSize(new Dimension(10,10));
        filler.setMaximumSize(filler.getPreferredSize());
        filler.setMinimumSize(filler.getPreferredSize());
        controlsWrap.add(filler, BorderLayout.CENTER);
        controlsWrap.add(previousButton, BorderLayout.NORTH);

        buttonWrap.add(controlsWrap, BorderLayout.SOUTH);

        JPanel northWrap = new JPanel(new BorderLayout());
        northWrap.add(backButton, BorderLayout.CENTER);

        northWrap.add(pageIndexLabel, BorderLayout.SOUTH);

        buttonWrap.add(northWrap, BorderLayout.NORTH);


        add(textArea, BorderLayout.CENTER);
        add(buttonWrap, BorderLayout.EAST);
        buttonWrap.setBorder(BorderFactory.createEmptyBorder(10,0,10,10));
    }

}
