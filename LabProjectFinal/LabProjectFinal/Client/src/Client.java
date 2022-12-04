
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import javax.swing.*;
import layout.TableLayout;

public class Client implements ActionListener {
  
  String host = "";
  BufferedReader bufferedReader;
  PrintWriter printWriter;
  int self, opponent;
  int player;

  JFrame frame;
  JPanel panel1, panel2, top, mid1, mid2, bottom;
  JButton signinButton, pickButton, leaveButton, resetButton;
  JTextArea textarea;
  JTextField usernameField, chatField;
  JRadioButton button1, button2, button3;
  JLabel userJLabel, icon1, icon2, icon3, selfJLabel, opponentJLabel, result;
  ImageIcon picIcon0, picIcon1, picIcon2, picIcon3, picIcon4;

  private void createDialog() {
    JTextField hostname = new JTextField("localhost");
    final JComponent[] inputs = new JComponent[] {
        new JLabel("Enter Serverhost address:"), hostname, };
    JOptionPane.showMessageDialog(null, inputs, "Server", JOptionPane.PLAIN_MESSAGE);
    host = hostname.getText();
  }

  private void createComponents() {
    panel1 = new JPanel();
    panel2 = new JPanel();
    top = new JPanel();
    mid1 = new JPanel();
    mid2 = new JPanel();
    bottom = new JPanel();

    picIcon0 = new ImageIcon(getClass().getResource("images/default1.jpg"));
    picIcon1 = new ImageIcon(getClass().getResource("images/default2.jpg"));
    picIcon2 = new ImageIcon(getClass().getResource("images/rock.jpg"));
    picIcon3 = new ImageIcon(getClass().getResource("images/paper.jpg"));
    picIcon4 = new ImageIcon(getClass().getResource("images/scissors.jpg"));

    signinButton = new JButton("Sign in");
    signinButton.addActionListener(this);
    pickButton = new JButton("Select");
    pickButton.addActionListener(this);
    pickButton.setEnabled(false);
    resetButton = new JButton("Reset");
    resetButton.setEnabled(false);
    resetButton.addActionListener(this);
    leaveButton = new JButton("Leave");
    leaveButton.addActionListener(this);

    icon1 = new JLabel(picIcon0);
    icon2 = new JLabel(picIcon1);
    icon2.setVisible(false);
    icon3 = new JLabel(picIcon1);
    selfJLabel = new JLabel("SELF", SwingConstants.CENTER);
    selfJLabel.setLabelFor(icon1);
    opponentJLabel = new JLabel("OPPONENT", SwingConstants.CENTER);
    opponentJLabel.setLabelFor(icon2);
    result = new JLabel("", SwingConstants.CENTER);

    button1 = new JRadioButton("Rock");
    button1.addActionListener(this);
    button2 = new JRadioButton("Paper");
    button2.addActionListener(this);
    button3 = new JRadioButton("Scissor");
    button3.addActionListener(this);
    button1.setEnabled(false);
    button2.setEnabled(false);
    button3.setEnabled(false);
    ButtonGroup group = new ButtonGroup();
    group.add(button1);
    group.add(button2);
    group.add(button3);

    textarea = new JTextArea("", 25, 20);
    textarea.setEditable(false);
    textarea.setLineWrap(true);
    textarea.setWrapStyleWord(true);
    
    usernameField = new JTextField(20);
    usernameField.addActionListener(this);
    
    chatField = new JTextField(20);
    chatField.addActionListener(this);
    chatField.setEditable(false);
    
    userJLabel = new JLabel("User: ");
    userJLabel.setLabelFor(usernameField);
    userJLabel.setHorizontalAlignment(4);

  }

  private void createFrame() {
    frame = new JFrame("Rock Paper Scissors");
    frame.setMinimumSize(new Dimension(600, 300));
    frame.setResizable(false);

    double f = TableLayout.FILL;
    double p = TableLayout.PREFERRED;
    
    // {row} {column} 
    double[][] t1 = { { 3, f, 3 },
        { 3, p, 3, f, 3, p, 3, p, 3 }
    };

    double[][] t2 = { { 3, p, 3, 100, 3, p, 3 },
        { 3, p, 25, p, 3 }
    };

    double[][] t3 = { { 3, p, 3, f, 3, p, 3 }, 
        { 3, p, 5, f, 5, p, 3 }
    };

    TableLayout layout1 = new TableLayout(t1);
    TableLayout layout2 = new TableLayout(t2);
    TableLayout layout3 = new TableLayout(t3);

    mid1.setLayout(layout2);
    mid1.add(icon1, "1,1");
    mid1.add(icon2, "5,1");
    mid1.add(icon3, "5,1");
    mid1.add(result, "3,1");
    mid1.add(selfJLabel, "1,3");
    mid1.add(opponentJLabel, "5,3");

    mid2.setLayout(new FlowLayout());
    mid2.add(button1);
    mid2.add(button2);
    mid2.add(button3);

    bottom.setLayout(new FlowLayout());
    bottom.add(pickButton);
    bottom.add(resetButton);
    bottom.add(leaveButton);

    panel1.setLayout(layout1);
    panel1.add(top, "1,1");
    panel1.add(mid1, "1,3");
    panel1.add(mid2, "1,5");
    panel1.add(bottom, "1,7");

    panel2.setLayout(layout3);
    panel2.add(userJLabel, "1,1");
    panel2.add(usernameField, "3,1");
    panel2.add(signinButton, "5,1");
    panel2.add(new JScrollPane(textarea), "1,3, 5,3");
    panel2.add(chatField, "1,5,5,5");

    frame.setLayout(new BorderLayout());
    frame.add(panel1, BorderLayout.WEST);
    frame.add(panel2, BorderLayout.EAST);

    frame.pack();
    frame.setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();    
      
    if (src == signinButton) {
      if (usernameField.getText().length() != 0) {
        usernameField.setEditable(false);
        signinButton.setEnabled(false);
        chatField.setEditable(true);
      }
    } 
    else if (src == usernameField) {
      if (usernameField.getText().length() != 0) {
        usernameField.setEditable(false);
        signinButton.setEnabled(false);
        chatField.setEditable(true);
      }
    } 
    else if (src == chatField) {
      if (chatField.getText().length() != 0) {
        printWriter.println(chatField.getText());
        chatField.setText("");
      }
    }

    else if (src == pickButton) {
      pickButton.setEnabled(false);
      button1.setEnabled(true);
      button2.setEnabled(true);
      button3.setEnabled(true);
    }

    else if (src == resetButton) {
      if (player == 1)  pickButton.setEnabled(true);
      
      button1.setEnabled(false);
      button2.setEnabled(false);
      button3.setEnabled(false);
      result.setText("");
      resetButton.setEnabled(false);
      icon2.setIcon(picIcon1);
      icon2.setVisible(false);
    }

    else if (src == leaveButton)    System.exit(0);

    else if (src == button1) {
      icon1.setIcon(picIcon2);
      self = 1;
      sendData();
    } 
    else if (src == button2) {
      icon1.setIcon(picIcon3);
      self = 2;
      sendData();
    } 
    else if (src == button3) {
      icon1.setIcon(picIcon4);
      self = 3;
      sendData();
    }
  }

  public Client() {
    createDialog();
    Socket socket;
    
    try {
      socket = new Socket(host, 1235);
      bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      printWriter = new PrintWriter(socket.getOutputStream(), true);
      } catch (IOException e) {
      System.err.println("Connection refused, server not found?\nClosing Application.");
      System.exit(0);
    }

    createComponents();
    createFrame();
  }

  
  private void run() {
    String nameString = null;
    while (usernameField.isEditable()) {
      System.out.print("");
      if (usernameField.isEditable() == false) {
        nameString = usernameField.getText();
        break;
      }
    }

    try {
      while (true) {
        String lineString;
        lineString = bufferedReader.readLine();
        if (lineString.startsWith("SUBMITNAME"))    printWriter.println(nameString);
        else if (lineString.startsWith("NAMEACCEPTED")) chatField.setEditable(true);
        else if (lineString.startsWith("RULE")) textarea.append(lineString.substring(5) + "\n\n");
        else if (lineString.startsWith("WELCOME")) {
          textarea.append(lineString.substring(8) + "!\n\n");
          player = Integer.parseInt(lineString.substring(23, 24));
        } 
        else if (lineString.startsWith("MESSAGE")) {
          textarea.append(lineString.substring(8) + "\n");
          textarea.setCaretPosition(textarea.getDocument().getLength());
        } 
        else if (lineString.startsWith("PLAYER1")) {
          if (player == 1) {
            pickButton.setEnabled(true);
          }
        } 
        else if (lineString.startsWith("OPPONENT_PLAYED")) {
          if (player == 2) {
            pickButton.setEnabled(true);
          }
          opponent = Integer.parseInt(lineString.substring(15));
          textarea.append("Opponent has chosen\n");
          switch (opponent) {
          case 1:
            icon2.setIcon(picIcon2);
            break;
          case 2:
            icon2.setIcon(picIcon3);
            break;
          case 3:
            icon2.setIcon(picIcon4);
            break;
          }
        } else if (lineString.startsWith("RESULT")) {
          result.setText(lineString.substring(6));
          textarea.append(lineString.substring(6) + '\n');
        } else {}
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void sendData(){

    printWriter.println(String.format("MOVE%d", self));
    button1.setEnabled(false);
    button2.setEnabled(false);
    button3.setEnabled(false);
    icon2.setVisible(true);
    resetButton.setEnabled(true);
  }

  public static void main(String[] args) throws Exception {
    Client client = new Client();
    client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    client.frame.setVisible(true);
    client.run();
  }

}