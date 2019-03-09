
import java.security.*;
import java.util.Vector;

public class Transaction {
    PublicKey to,from;
    public String signature;
    int amount;
    Transaction(PublicKey me,PublicKey you,int val){
        to=you;
        this.from=me;
        amount=val;
        System.out.println(getcopy());
        }
    Transaction(PublicKey you,PublicKey me,String sign,int val){
        //hn3ml eh fl index
        to=you;
        this.from=me;
        signature=sign;
        amount=val;
    }
    public Vector<String> getcopy(){
        Vector<String> content=new Vector<>();
        content.add(Global.KeyToString(to));
        content.add(Global.KeyToString(from));
        content.add(amount+"");
        content.add(signature);
        return content;
    }
    public String getTransaction(){
       return Global.KeyToString(from)+Global.KeyToString(to)+amount;
    }

}
