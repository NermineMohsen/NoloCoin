import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Block {
    //add the creator??
    public int index, dummy = 0;
    public String Hash, PrevHash;   //  ADD TO HASH TO MINEEEEEEEEEE String trialInt;
    public Transaction data;
    public String timestamp; //= new Timestamp()//(System.currentTimeMillis());
    Block(){}
    Block(String prev, Transaction data, int indx) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddhhmmss");
        timestamp=simpleDateFormat.format(new Date());
        PrevHash = prev;
        this.data = data;
        index = indx;
        Hash = hash_block();
        System.out.println(blockToString());
    }
    Block(String prev, int indx) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddhhmmss");
        timestamp=simpleDateFormat.format(new Date());
        PrevHash = prev;
        index = indx;
        Hash = hash_block();
        System.out.println("hash: "+Hash);
    }
    public String hash_block() {
        Sha hashgenerator = new Sha();
        if (data != null)
            return hashgenerator.hash256(dummy + index + PrevHash + data.getTransaction() + timestamp.toString());
        else
            return hashgenerator.hash256(dummy + index + PrevHash + timestamp.toString());
    }
    public void Mine(int level) {
        String comp = "";
        for (int i = 0; i < level; i++) {
            comp += "0";
        }
        ///00000
        System.out.println("mining");
        while (Hash.substring(0, level).equals(comp)) {
            dummy++;
            System.out.println(Hash);
            Hash = hash_block();
        }
        System.out.println("block " + index + " has been mined ! ");
        ///mine till the level is reached
    }
    public String blockToString(){
        String all=index+" "+Hash+" "+PrevHash+" "+dummy+" "+timestamp;
        Vector<String> trans=data.getcopy();
        all+=" "+Global.KeyToString(data.to)+" "+Global.KeyToString(data.from)+" "+data.amount+" "+data.signature;
        System.out.println("BLOCKKKKKK :"+all);
        return all;
    }
    public static int read(String str){
        String[] splited = str.split("\\s+");
        Vector<String> blockcontent=new Vector<>();
        System.out.println("BLOCK:     ");
        for (int i=0;i<splited.length;i++)
        {
            blockcontent.add(splited[i]);
            System.out.println(splited[i]);
        }
        System.out.println("------------------------------");
        Block back=Chain.extractBlock(blockcontent);
        Account.recieve(back.data);
        if (back!=null){Chain.addBlock(back);
        return 0;
        }
        System.out.println("amount:  "+back.data.amount);
        return back.data.amount;
    }
}
