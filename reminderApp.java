import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.awt.TrayIcon.MessageType;
import javax.swing.*;
import java.text.*;

class reminderThread extends Thread {
    
    static String finalTime;
    static String task;

    public void setTime(String tempTime) {
        finalTime = tempTime;
    }

    public void setTask(String tempTask) {
        task = tempTask;
    }

    

    public void run() {
        System.out.println("Added to queue. You will be reminded to do " + task + " at " + finalTime + ".");
        Boolean isLate = false;
        while (!isLate) {
            LocalTime now = LocalTime.now();
            LocalTime limit = LocalTime.parse( finalTime );
            isLate = now.isAfter( limit );
            if (isLate) {
                if (SystemTray.isSupported()) {
                    reminderApp td = new reminderApp();
                    try {
                        td.displayTray(task);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("System tray not supported!");
                }
                break;
            }
        }
        stop();
    }

    
}

public class reminderApp extends JPanel {
    static String timeStr;
    static String taskStr;

    public static void main(String[] args) throws AWTException {
        JFrame f=new JFrame("Reminder Thingy");  
        JButton b=new JButton("Create Reminder");

        
        Format timeFormat = new SimpleDateFormat("HH:mm");
        final JFormattedTextField time=new JFormattedTextField(timeFormat);
        final JTextField task = new JTextField();
        final JLabel queue = new JLabel();
        queue.setText("Reminders:");

        time.setBounds(50,50, 150,20);  
        task.setBounds(50,75,150,20);
        b.setBounds(50,100,150,20);  
        queue.setBounds(50,125,150, 16);
        f.add(b);
        f.add(queue);
        f.add(time);
        f.add(task);
        f.setSize(400,400);  
        f.setLayout(null);  
        f.setVisible(true);
        b.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
                        timeStr = time.getText();
                        taskStr = task.getText();
                        queue.setBounds(50,125,150,queue.getHeight()+16);
                        queue.setText("<html>"+queue.getText().replaceAll("<html>", "").replaceAll("</html>", "")+"<br/>"+taskStr+" - "+timeStr+"</html>");
                        reminderThread reminderFinal = new reminderThread();
                        reminderFinal.setTime(timeStr);
                        reminderFinal.setTask(taskStr);
                        reminderFinal.start();
            }  
        });  

            
        /* FOR NOTIFS
        if (SystemTray.isSupported()) {
            reminderApp td = new reminderApp();
            td.displayTray();
        } else {
            System.err.println("System tray not supported!");
        }
        */
    }

    public void displayTray(String task) throws AWTException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip(task);
        tray.add(trayIcon);

        trayIcon.displayMessage(task, "Reminder!!!! DO "+task+" NOW!!!!!", MessageType.INFO);
    }
}