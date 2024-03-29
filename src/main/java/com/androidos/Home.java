package com.androidos;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.*;
import java.util.List;
import java.awt.*;

import com.androidos.clock.Clock;
import com.androidos.clock.Time;
import com.androidos.stopwatch.Stopwatch;
import com.androidos.timer.TimerGUI;

import com.androidos.app.App;
import com.androidos.app.AppButton;

import static com.androidos.data.Data.*;
import static com.androidos.helper.Style.*;

public class Home implements Runnable, ActionListener{ 

    private JFrame frame;

    private Time time;
    private JPanel panel;
    private JLabel homeClock;
    private List<AppButton> apps;
    public static Map<Thread, App> appThreads = new HashMap<>();

    public Home(JFrame frame) {
        this.frame = frame;

        time = new Time();
        panel = new JPanel(new BorderLayout());
        homeClock = new JLabel();
        apps = new ArrayList<>();
        setPanel();
    }

    private void setPanel() {
        storeApps();
        setAppsButtonProperties();
        setHomeClockStyle();
        setPanelStyle(panel);

        panel.add(BorderLayout.CENTER, homeClock);
        panel.add(BorderLayout.PAGE_END, getAppsInOnePanel());
    }

    private void storeApps() {
        apps.add(new AppButton(getClass(), STOPWATCH_ICON_URL, STOPWATCH_APP_NAME));
        apps.add(new AppButton(getClass(), TIMER_ICON_URL, TIMER_APP_NAME));
        apps.add(new AppButton(getClass(), CLOCK_ICON_URL, CLOCK_APP_NAME));
    }

    private void setAppsButtonProperties() {
        for (AppButton app : apps) {
            app.getButton().addActionListener(this);
        }
    }

    private void setHomeClockStyle() {
        homeClock.setForeground(Color.WHITE);
        homeClock.setHorizontalAlignment(SwingConstants.CENTER);
        homeClock.setFont(new Font("SERIF", 0, 50));
    }

    private JPanel getAppsInOnePanel() {
        JPanel appsPanel = new JPanel(new FlowLayout());

        for (AppButton button : apps) appsPanel.add(button.getButton());

        appsPanel.setComponentOrientation(
            ComponentOrientation.LEFT_TO_RIGHT);

        appsPanel.setBackground(Color.BLACK);
        return appsPanel;
    }

    @Override
    public void run() {
        while (true) {
            homeClock.setText(time.getFormatedTime());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        runApp(((JButton)e.getSource()).getName());
    }

    private void runApp(String name) {
        Thread t = isContainsThread(name);
        if (t != null) {
            replacePanel(appThreads.get(t).getPanel());
        } else {
            App app = getApp(name);
            t = new Thread((Runnable)app);
            t.setName(name);
            t.start();
            appThreads.put(t, app);
            replacePanel(app.getPanel());
        }
    }

    private Thread isContainsThread(String name) {
        for (Thread t : appThreads.keySet()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }

        return null;
    }

    private App getApp(String name) {

        switch(name) {
            case STOPWATCH_APP_NAME :
              return new Stopwatch(getNewJPanelWithBorderLayout());

            case TIMER_APP_NAME:
              return new TimerGUI(frame, getNewJPanelWithBorderLayout());

            default :
            return new Clock(getNewJPanelWithBorderLayout());
        }

    }

    private void replacePanel(JPanel app) {
        app.setBackground(Color.BLACK);
        app.setBounds(0,0,
        SCREEN_WIDTH,SCREEN_HEIGHT-BOTTOM_BAR_HEIGHT); 
        frame.getContentPane().remove(panel);
        frame.getContentPane().add(app);
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();
    }


    public JPanel getPanel() {
        return panel;
    }
    
}
