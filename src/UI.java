import org.w3c.dom.Text;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.sun.java.accessibility.util.AWTEventMonitor.addActionListener;

public class UI {

    private Peer chatPeer;
    private final static ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
    public String Selection;
    private ChatRoom selectedPeer;
    public static ArrayList<ChatRoom> chatPeers = new ArrayList<>();
    public static Chain BlockChain = new Chain();
  /*  private Executor swteExecutor = new Executor() {
        @Override
        public synchronized void execute(Runnable command) {

            Display.getDefault().syncExec(command);
        }
    };*/
    //---------------------------------------------------------------------
    private JPanel panel;
    private JButton Connect;
    private JTextArea chatText;
    private JComboBox Combo;
    private JSpinner Value;
    private JButton send;
    private JTextField Neme;
    public String getName(){
        return Neme.getText();
    }
    public UI(){
        chatPeer = new Peer(this, Executors.newSingleThreadExecutor());
        Connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startLocalPeer();

            }
        });
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(ChatRoom a:chatPeers){
                    a.sendMessage((Integer)Value.getValue(),selectedPeer.getDisplayName());
                }
            }
        });
        Combo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectChatPeer(Combo.getSelectedIndex());
            }
        });
    }
    public static void main(String[] args) {
        JFrame frame=new JFrame("UI");
        frame.setContentPane(new UI().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    public class MyRunnable implements Runnable {

        private int var;

        public MyRunnable(int var) {
            this.var = var;
        }

        public void run() {
            // code in the other thread, can reference "var" variable
        }
    }
    public void startLocalPeer() {
        chatPeer.start();
    }

    public void addChatPeer(ChatRoom chatPeer) {
        this.chatPeers.add(chatPeer);
        Combo.addItem(chatPeer.getDisplayName());
    }

    public void removeChatPeer(ChatRoom chatPeer) {
        this.chatPeers.remove(chatPeer);
        Combo.removeAllItems();
        for (ChatRoom peer : this.chatPeers) {
            Combo.addItem(peer.getDisplayName());
        }
    }
    public void selectChatPeer(int index) {
        if (index == -1 || index >= this.chatPeers.size()) {
            this.selectedPeer = null;
        } else {
            this.selectedPeer = this.chatPeers.get(index);
            this.updateChatData();
        }
    }

    public void updateChatData() {
        if (this.selectedPeer == null)
            return;

        this.chatText.setText(this.selectedPeer.getChatText());
    }


}

/*
*
* import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ui {
    private JTextArea chat;
    private JPanel panel;
    private JTextField message;
    private JButton send;
    public ui(){
        PeerClass a=new PeerClass();
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                a.send(message.getText());
            }
        });

    }



    public static void main(String[] args) {
        JFrame frame=new JFrame("ui");
        frame.setContentPane(new ui().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

* */
