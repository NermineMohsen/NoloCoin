
import de.tum.in.www1.jReto.Connection;
import de.tum.in.www1.jReto.RemotePeer;
import de.tum.in.www1.jReto.connectivity.InTransfer;
import de.tum.in.www1.jReto.connectivity.OutTransfer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.PublicKey;

/*
Each ChatRoom represents a chat conversation with the peer it represents.
For simplicity, when a chat room is created, a connection is created to the remote peer, which is used to send chat messages.
This means that both participating peers create a connection, i.e. there are two connections between the peers, even though one would be enough.
Files can be exchanged in using the ChatRoom class. To transmit a file, a new connection is established. First, the file name is transmitted, then the actual file.
After transmission, the connection is closed.
Therefore, the first incoming connection from a remote peer is used to receive chat messages; any further connections are used to receive files.
*/
public class ChatRoom {
    public static int longest = 0;
    public static ChatRoom trusted;
    private Peer me;
    private UI chatUI;

    /**
     * The display name of the local peer in the chat
     */
    private String localDisplayName;
    /**
     * The display name of the remote peer in the chat
     */
    private String remoteDisplayName;
    /**
     * The full text in the chat room; contains all messages.
     */
    private String chatText = "";
    /** The progress of a file if it one is being transmitted. */

    /**
     * The remotePeer object representing the other peer in the chat room (besides the local peer)
     */
    private RemotePeer remotePeer;
    /**
     * The Connection used to receive chat messages.
     */
    private Connection incomingConnection;
    /**
     * The Connection used to send chat messages.
     */
    private Connection outgoingConnection;
    private ChatRoom selectedPeer;

    public ChatRoom(RemotePeer remotePeer, Peer local, UI chatUI, String from) {
        this.me = local;
        localDisplayName = Global.KeyToString(local.acc.publicKey);
        System.out.println("my name "+localDisplayName);
        System.out.println("localname: " + localDisplayName);
        this.chatUI = chatUI;
        this.remotePeer = remotePeer;
        // When an incoming connection is available, call acceptConnection.
        this.remotePeer.setIncomingConnectionHandler((peer, connection) -> this.acceptIncomingConnection(connection, from));
        // Create a connection to the remote peer
        this.outgoingConnection = remotePeer.connect();
        // The first message sent through the outgoing connection contains the display name that should be used, so it is sent here.
        this.outgoingConnection.send(Global.StringToBuff(Global.KeyToString(local.acc.publicKey)));
        if (remoteDisplayName!=null){
            chatUI.addChatPeer(this);
        }

    }

    private void acceptIncomingConnection(Connection connection, String from) {
        if (this.incomingConnection == null) {
            // If this is the first connection, we use it to receive message data. Therefore we call handleMessageData when data was received.
            this.incomingConnection = connection;
            this.incomingConnection.setOnData((t, data) -> handleMessageData(data));
        }
    }

    public void sendMessage(int Val, String publick) {
        Block sent=me.acc.Send(Val, Global.StringToPublickey(publick));
        if ( sent!= null) {
            System.out.println("sending :");
            System.out.println(sent.blockToString());
            this.outgoingConnection.send(Global.StringToBuff(sent.blockToString()));
            appendChatMessage(this.localDisplayName, Val + "");
        } else {
            System.out.println("not sent");
        }
    }

    private void handleMessageData(ByteBuffer data) {
        String message = Global.BuffToString(data);
        System.out.println("message: "+message);
        if (Global.isInteger(message)) {
            if (message == "-1") { //RemotePeer requested size of blockchain
                if (Chain.check()) {
                    this.outgoingConnection.send(Global.StringToBuff("SIZE:" + UI.BlockChain.len()));
                } else {
                    this.outgoingConnection.send(Global.StringToBuff("SIZE: -1"));
                }
            } else if (message == "-2") { ///send all my blocks
            }
        } else if (message.substring(0, 5).equals("SIZE:")) {
            if (Global.isInteger(message.substring(5))) {
                int siz = Integer.parseInt(message.substring(5));
                if (longest < siz) {
                    siz = longest;
                    this.outgoingConnection.send(Global.StringToBuff("-2"));
                }
            }
        } else {
            if (this.remoteDisplayName == null) {
                System.out.println("message:::::::::::::::: " + message);
                this.setDisplayName(message);
                appendChatMessage("connected to ",this.remoteDisplayName);
            } else {
                appendChatMessage(this.remoteDisplayName," Sent you: "+ Block.read(message));
            }
        }
    }

    private void appendChatMessage(String displayName, String message) {
        this.chatText += displayName + ": " + message + "\n";
        chatUI.updateChatData();
    }

    private void setDisplayName(String displayName) {
        this.remoteDisplayName = displayName;
        chatUI.addChatPeer(this);
    }
    public String getDisplayName() {
        System.out.println("name: "+remoteDisplayName);
        return this.remoteDisplayName;
    }

    public String getChatText() {
        return this.chatText;
    }

}