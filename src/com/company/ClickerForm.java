package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.*;
import java.util.Properties;

public class ClickerForm {
    private JFrame jFrame;
    private JPanel ClickerForm;

    private JLabel Xcoordinate;
    private JLabel Ycoordinate;
    private JLabel IntervalLabel;

    private JTextField xCoordinateField;
    private JTextField yCoordinateField;
    private JTextField intervalField;

    private JButton startButton;
    private JButton stopButton;

    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenu menuHelp;
    private JMenuItem menuSettingItem;
    private JMenuItem menuExitItem;
    private JMenuItem menuAboutItem;

    private Boolean start;
    private Boolean threadStarted;
    private Thread clickThread;
    private final Robot robot;
    private TempFile tempFile = null;

    public ClickerForm() throws AWTException, IOException {
        super();
        this.robot = new Robot();
        jFrame = new JFrame();
        setJFrameSettings();
        setButtonActions();
        setTempData();
    }

    private void setJFrameSettings() {
        jFrame.setTitle("Auto Clicker");//Setting title of JFrame
        jFrame.setSize(400,400);//Setting Size
        jFrame.setLocationRelativeTo(null);//Setting Location center
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Setting default close operation
        jFrame.setJMenuBar(setJFrameMenuSetting());
        jFrame.setContentPane(ClickerForm);
        jFrame.pack();
        jFrame.setResizable(false);
        ImageIcon icon = new ImageIcon(ClickerForm.class.getClassLoader().getResource("daramee.jpg"));
        jFrame.setIconImage(icon.getImage());
        jFrame.setVisible(true); //Setting visibility
    }

    private JMenuBar setJFrameMenuSetting() {
        menuBar = new JMenuBar();
        menuFile = new JMenu("File");
        menuHelp = new JMenu("Help");

        menuSettingItem = new JMenuItem("Save Setting");
        menuSettingItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempFile = new TempFile(xCoordinateField.getText(), yCoordinateField.getText(), intervalField.getText());
                try {
                    saveSettings(tempFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        menuExitItem = new JMenuItem("Exit");
        menuExitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });
        menuFile.add(menuSettingItem);
        menuFile.add(menuExitItem);

        menuAboutItem = new JMenuItem("About");
        menuAboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(jFrame, "오토 클릭해주는 프로그램입니다 ㅎㅎㅎ. \n 잘 사용해주세요 ^___^");
            }
        });
        menuHelp.add(menuAboutItem);

        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        return menuBar;
    }

    private void setTempData() throws IOException {
        String xCoordinate = retrieveSettings("x-coordinate");
        String yCoordinate = retrieveSettings("y-coordinate");
        String time = retrieveSettings("time-interval");
        if (!xCoordinate.isEmpty()) { xCoordinateField.setText(xCoordinate); }
        if (!yCoordinate.isEmpty()) { yCoordinateField.setText(yCoordinate); }
        if (!time.isEmpty()) { intervalField.setText(time); }
    }

    private void setButtonActions() {
        stopButton.setEnabled(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (xCoordinateField.getText().equals("") || yCoordinateField.getText().equals("") || intervalField.getText().equals("")) {
                    JOptionPane.showMessageDialog(jFrame, "Please put all input fields");
                    return;
                }
                Integer xCoordinate = 0;
                Integer yCoordinate = 0;
                Integer time = 0;
                try {
                    xCoordinate = setInputField(xCoordinateField, Xcoordinate.getText());
                    yCoordinate = setInputField(yCoordinateField, Ycoordinate.getText());
                    time = setInputField(intervalField, IntervalLabel.getText());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                start = true;
                threadStarted = false;
                if (start) {
                    clickThread = createClickThread(xCoordinate, yCoordinate, time);
                }
                if (!threadStarted) {
                    clickThread.start();
                }
                stopButton.setEnabled(true);
                startButton.setEnabled(false);
                xCoordinateField.setEditable(false);
                yCoordinateField.setEditable(false);
                intervalField.setEditable(false);
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickThread.stop();
                start = false;
                threadStarted = true;
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                xCoordinateField.setEditable(true);
                yCoordinateField.setEditable(true);
                intervalField.setEditable(true);
            }
        });
    }

    private Integer setInputField(JTextField field, String label) throws Exception {
        Integer value = 0;
        try {
            value = (label.equals("Time in Minute")) ?
                    Integer.valueOf(field.getText()) * 1000 * 60 :
                    Integer.valueOf(field.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(startButton, label + " field format is not correct");
            throw new NumberFormatException("input field exception");
        }
        return value;
    }

    private Thread createClickThread(int x, int y, int time) {
        return new Thread(() -> {
            while (true) {
                try {
                    moveCursorAndClick(x, y, robot);
                    Thread.sleep(time);
                } catch (final Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void moveCursorAndClick(int x, int y, Robot robot) {
        robot.mouseMove(x, y);
        robot.delay(500);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private void saveSettings(TempFile tempFile) throws IOException {
        if (tempFile.getxCoordinator().isEmpty() && tempFile.getyCoordinator().isEmpty() && tempFile.getTimeInterval().isEmpty()) {
            JOptionPane.showMessageDialog(jFrame, "Unable to save empty fields");
            return;
        }
        File configFile = new File("config.properties");
        Properties properties = new Properties();
        properties.setProperty("x-coordinate", tempFile.getxCoordinator());
        properties.setProperty("y-coordinate", tempFile.getyCoordinator());
        properties.setProperty("time-interval", tempFile.getTimeInterval());
        FileWriter writer = new FileWriter(configFile);
        properties.store(writer, "settings");
        writer.close();
    }

    private String retrieveSettings(String key) throws IOException {
        File configFile = new File("config.properties");
        String value = "";
        if (configFile.exists()) {
            FileReader reader = new FileReader(configFile);
            Properties props = new Properties();
            props.load(reader);
            value = props.getProperty(key);
        }
        return value;
    }
}
