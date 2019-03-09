import de.tum.in.www1.jReto.LocalPeer;
import de.tum.in.www1.jReto.RemotePeer;
//import de.tum.in.www1.jReto.module.remoteP2P.RemoteP2PModule;
import de.tum.in.www1.jReto.module.wlan.WlanModule;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;



public class Peer {
    public Account acc;
    private LocalPeer localPeer;
    private Map<RemotePeer, ChatRoom> chatPeers = new HashMap<>();
    public String displayName;
    private UI chatUI;
    Peer(UI chatUI, Executor mainThreadExecutor) {
        this.chatUI = chatUI;
        acc = new Account(256, 100);
        try {
            this.initializeLocalPeer(mainThreadExecutor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeLocalPeer(Executor executor) throws Exception {
        /**
         * Create a local peer with a WlanModule. To use the RemoteP2PModule, the RemoteP2P server needs to be deployed locally.
         */
        //RemoteP2PModule remoteModule = new RemoteP2PModule(new URI("ws://localhost:8080/"));
        WlanModule wlanModule = new WlanModule("SimpleP2PChat");
        this.localPeer = new LocalPeer(Arrays.asList(wlanModule), executor);
    }

    /**
     * Starts the local peer.
     * When a peer is discovered, a ChatRoom with that peer is created, when one is lost, the corresponding ChatRoom is removed.
     */
    public void start() {
        this.displayName = Global.KeyToString(this.acc.publicKey);
        this.localPeer.start(
                peer -> createChatPeer(peer),
                peer -> removeChatPeer(peer)
        );

    }

    public void createChatPeer(RemotePeer peer) {

        if (this.chatPeers.get(peer) != null) {
            System.err.println("We already have a chat peer for this peer!");
            return;
        }
        System.out.println("added?");
        ChatRoom chatPeer = new ChatRoom(peer, this, this.chatUI,this.chatUI.Selection);
        System.out.println("added!");
        this.chatPeers.put(peer, chatPeer);
        System.out.println("added.");

        ///////////send all data
    }
    public void removeChatPeer(RemotePeer peer) {
        this.chatUI.removeChatPeer(this.chatPeers.get(peer));
        this.chatPeers.remove(peer);
    }
}