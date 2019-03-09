import java.io.*;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.Vector;

public class Chain {

    public static int level = 2;
    static Vector<Block> BlockChain = new Vector<>();
    static Block genesis;

    Chain() {
        //add genesis block
        createGenesis();
    }

    static void createGenesis() {
        genesis = new Block("0", 0);
        BlockChain.add(genesis);
        System.out.println("genesis hash" + genesis.Hash);
    }

    public static void addBlock(Block a) {
        BlockChain.add(a);
        Account.recieve(a.data);
    }

    public static Block add(Transaction data) { //for sending
        System.out.println("Data?");
        System.out.println(data.signature + ":" + data.amount + ":" + data.from + ":" + data.to + ":");
        System.out.println("size+" + BlockChain.size());
        System.out.println(BlockChain.lastElement().Hash + "---");
        Block a = new Block(BlockChain.lastElement().Hash, data, BlockChain.size());
        a.Mine(level);
        BlockChain.add(a);
        save();
        if (data == null) {
            return null;
        }
        return a;
    }

    public static Boolean check() {
        Boolean trusted = true;
        String comp = "";
        for (int i = 0; i < level; i++) {
            comp += "0";
        }
        for (int i = 1; (i) < BlockChain.size(); i++) {
            if (BlockChain.get(i).PrevHash.equals(BlockChain.get(i - 1).Hash) == false || BlockChain.get(i).Hash.equals(BlockChain.get(i).hash_block()) == false || BlockChain.get(i).Hash.substring(0, level).equals(comp) == false) {
                System.out.println("block " + i + " is corrupted");
                trusted = false;
            }
        }
        return trusted;
    }

    public static Block extractBlock(Vector<String> content) {
        System.out.println("size "+content.size());
        if (content.size() < 8) {
            System.out.println("not enough");
            return null;
        }
        Block a = new Block();
        a.index = Integer.parseInt(content.get(0).trim());
        a.Hash = content.get(1);
        a.PrevHash = content.get(2);
        a.dummy = Integer.parseInt(content.get(3).trim());
        a.timestamp = (content.get(4));
        a.data = extractTrans(content);
        return a;
    }

    private static Transaction extractTrans(Vector<String> content) {
        String sign = "";
        for (int i = 8; i < content.size(); i++) {
            System.out.println(content.get(i));
            sign += content.get(i);
        }
        return new Transaction(Global.StringToPublickey(content.get(5)), Global.StringToPublickey(content.get(6)), sign, Integer.parseInt(content.get(7)));
    }

    public int len() {
        return BlockChain.size();
    }

    public static void save() { ///pass my key or a name as the file name;
        BufferedWriter writer = null;
        String MyName = Global.KeyToString(Account.publicKey);
        try {
            writer = new BufferedWriter(new FileWriter("D:\\done\\Desktop\\BlockChain\\Peer.txt"));

            for (Block now : BlockChain) {
                if (now.data == null) {
                    continue;
                }
                writer.newLine();
                writer.write(now.index + " ");
                writer.newLine();
                writer.write(now.Hash + " ");
                writer.newLine();
                writer.write(now.PrevHash + " ");
                writer.newLine();
                writer.write(now.dummy + " ");
                writer.newLine();
                writer.write(now.timestamp + " ");
                writer.newLine();
                Vector<String> back = now.data.getcopy();
                for (String out : back) {
                    System.out.println(out + "");
                    writer.write(out + " ");
                    writer.newLine();
                }
                writer.write("#$# ");
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reader() {
        String MyName = Global.KeyToString(Account.publicKey);
        Vector<String> back = new Vector<>();
        BufferedReader reader;
        String path = "D:\\done\\Desktop\\BlockChain\\Peer.txt";
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            System.out.println("////////////////////////////////////////////");
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                back.add(line.substring(0, line.length() - 1));
                line = reader.readLine();
            }
            System.out.println("////////////////////////////////////////////");
            reader.close();
         //   back.remove(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BlockChain.clear();
        createGenesis();
        Vector<String> content = new Vector<>();
        for (int i = 0; i < back.size(); i++) {
            if (back.get(i).equals("#$#")) {
                System.out.println("BLOCK IS DONE");
                Block nulltester = extractBlock(content);
                if (nulltester != null) {
                    BlockChain.add(nulltester);

                }content.clear();
                continue;
            }
            System.out.println(back.get(i));
            content.add(back.get(i));
        }
        for (int i = 0; i < BlockChain.size(); i++) {
            // BlockChain.get(i).index = i;
            System.out.println("blockssssssss " + BlockChain.get(i).Hash + "?????????????" + BlockChain.get(i).index);
        }
    }
}

