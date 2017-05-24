package com;
    import java.awt.GridLayout;
    import java.awt.event.KeyListener;
    import java.awt.event.WindowAdapter;
    import java.awt.event.WindowEvent;
    import java.io.File;
    import java.io.FileWriter;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.event.KeyEvent;
    import java.util.ArrayList;

    import javax.swing.JButton;
    import javax.swing.JFrame;
    import javax.swing.JLabel;
    import javax.swing.JPanel;
    import javax.swing.JTextField;

public class KeystrokeLogger extends JFrame implements KeyListener {

    private static final long serialVersionUID = 1L;

    private JFrame mainFrame;
    private JPanel textPanel;
    private JPanel inputPanel;
    private JPanel btnPanel;
    private JLabel textArea;
    private JTextField jtfInput;
    private JButton okBtn;
    private JButton canBtn;

    private long pressTime;
    private long releaseTime;
    private long pressDuration;

    private static ArrayList<Character> keyEvents;
    private static ArrayList<Long> pressEvents;
    private static ArrayList<Long> releaseEvents;
    private static ArrayList<Long> holdTime;
    private static ArrayList<Long> ppTime;
    private static ArrayList<Long> prTime;

    private static boolean append;

    private static final String TEXT_MESSAGE = "Type 'hello world' and Click Okay";

    private static File textFile;

    public KeystrokeLogger() { createGui(); }

    private void createGui() {  // 创建"KeyStroke Dynamics"GUI
        try {
            mainFrame = new JFrame("Keystroke Dynamics");
            mainFrame.setSize(1000, 200);
            mainFrame.setLayout(new GridLayout(3, 1));

            mainFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent windowEvent) {
                    System.exit(0);
                }
            });

            textPanel = new JPanel();
            inputPanel = new JPanel();
            btnPanel = new JPanel();
            mainFrame.add(textPanel);
            mainFrame.add(inputPanel);
            mainFrame.add(btnPanel);

            textArea = new JLabel(TEXT_MESSAGE);
            jtfInput = new JTextField(20);
            jtfInput.addKeyListener(this);
            textPanel.add(textArea);
            inputPanel.add(jtfInput);

            okBtn = new JButton("Okay");
            canBtn = new JButton("Cancel");
            okBtn.setActionCommand("Okay");
            canBtn.setActionCommand("Cancel");
            okBtn.addActionListener(new ButtonClickListener());
            canBtn.addActionListener(new ButtonClickListener());
            btnPanel.add(okBtn);
            btnPanel.add(canBtn);

            mainFrame.setVisible(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) { //  获得按下的字母并记录到KeyEvents中
        try {
            Character ch = new Character(ke.getKeyChar());
            System.out.println("KeyTyped = " + ch.charValue());
            keyEvents.add(ch);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {   //  记录按下某一按键的时刻
        try {
            Character ch = new Character(KeyEvent.getKeyText(ke.getKeyCode()).toCharArray()[0]);
            System.out.println("KeyPressed = " + ch.charValue());
            System.out.print("Time in milliseconds for key pressed = ");
            pressTime = System.currentTimeMillis();
            System.out.println(pressTime);

            pressEvents.add(pressTime);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {  //  记录松开某一按键的时刻
        try {
            Character ch = new Character(KeyEvent.getKeyText(ke.getKeyCode()).toCharArray()[0]);
            System.out.println("KeyReleased = " + ch);
            System.out.print("Time in milliseconds for key released = ");
            releaseTime = System.currentTimeMillis();
            System.out.println(releaseTime);
            pressDuration = releaseTime - pressTime;    // 获取持续时间
            System.out.println("Duration :" + pressDuration);

            releaseEvents.add(releaseTime);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            try {
                String command = ae.getActionCommand();
                if(command.equals("Okay")) {
                    mainFrame.dispose();
                    processLogs();
                }
                else if(command.equals("Cancel")) {
                    mainFrame.dispose();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processLogs() {    // 记录用户所打字的内容,每个按键的持续时间及连续两次按键之间的时间间隔
        try {
            StringBuilder sb = new StringBuilder();
            //sb.append("Character,PressTime,ReleaseTime,Duration\n");

            String headers = "KeyStroke Dynamics Logs\n\n";

            sb.append(headers);

            StringBuilder sb1 = new StringBuilder();


            System.out.println("\nSize : " + keyEvents.size() + "\n\n");
            System.out.println("\n\n\n--------------------PROCESS LOGS----------------------\n\n");

            for(int i=0; i<keyEvents.size(); i++) {
                System.out.print(keyEvents.get(i).charValue()+ "\t");
                System.out.print(pressEvents.get(i).longValue() + "\t");
                System.out.print(releaseEvents.get(i).longValue() + "\t");
                System.out.print(releaseEvents.get(i).longValue() - pressEvents.get(i).longValue() + "\t"); // 某一次按键的持续时间
                if (i != 0) {
                    System.out.print(releaseEvents.get(i - 1).longValue() - pressEvents.get(i).longValue());    //  某一次按键与之前一次松开按键的时间间隔
                }
                System.out.print("\n");
                holdTime.add(new Long(releaseEvents.get(i).longValue() - pressEvents.get(i).longValue()));  // 某一次按键的持续时间
            }

            for(int i=1; i<keyEvents.size(); i++) {
                ppTime.add(new Long(pressEvents.get(i).longValue() - pressEvents.get(i-1).longValue()));    // 某一次按键与之前一次按键的时间间隔
                prTime.add(new Long(releaseEvents.get(i-1).longValue() - pressEvents.get(i).longValue()));  // 某一次按键与之前一次松开按键的时间间隔
            }

            sb1.append("Content:\nchar\tholdtime\tppTime\tprTime\n" + keyEvents.get(0).charValue() + "\t\t" + holdTime.get(0).longValue() + "\n");

            for(int i=0; i<ppTime.size(); i++) {
                sb1.append(keyEvents.get(i+1).charValue() + "\t\t" +  holdTime.get(i+1).longValue() + "\t\t\t" + ppTime.get(i).longValue() + "\t\t" + prTime.get(i).longValue() + "\n");
            }

            sb1.append("\n");

            textFile = new File("keystroke-dynamics.txt");
            if(!textFile.exists()) {
                textFile.createNewFile();
                append = false;
            } else {
                append = true;
            }
            FileWriter fw = new FileWriter(textFile, true);
            if(append) {
                fw.write(sb1.toString());
            } else {
                fw.write(sb.toString());
                fw.write(sb1.toString());
            }
            fw.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            keyEvents = new ArrayList<Character>();
            pressEvents = new ArrayList<Long>();
            releaseEvents = new ArrayList<Long>();
            holdTime = new ArrayList<Long>();
            ppTime = new ArrayList<Long>();
            prTime = new ArrayList<Long>();

            KeystrokeLogger keystrokeLogger = new KeystrokeLogger();
            keystrokeLogger.pack();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}