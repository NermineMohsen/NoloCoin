import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Vector;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Account {

    public static PublicKey publicKey;
    private PrivateKey privateKey;
    int size, funds;
    Cipher cipher;
    static Vector<Transaction> transactionsSent, transactionsRecieved;
    Account(int siz, int money){
        funds = money;
        Security.addProvider(new BouncyCastleProvider());
        transactionsRecieved = new Vector<>();
        transactionsSent = new Vector<>();
        size = siz;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("ECDSA", "BC");   //SMALLER SIZE WITH SAME SECURITY OR EVEN BETTER THAN THE RSA (Rivest, Shamir, and Adelman) Elliptic Curve Digital Signature Algorithm
            SecureRandom rand = SecureRandom.getInstance("SHA1PRNG"); //faster than NativePRNG bec it has sha+counter+SEED
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            generator.initialize(ecSpec, rand);
            KeyPair pair = generator.generateKeyPair();  /// generate key pair
            publicKey = pair.getPublic();
            privateKey = pair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        Chain.reader();
    }
    public String Sign(String input) {
        Signature signatureGenerator;
        byte[] output = new byte[0];
        try {
            signatureGenerator = Signature.getInstance("ECDSA", "BC");//Signature.getInstance("SHA1withECDSA");//Signature.getInstance("ECDSA", "BC");
            signatureGenerator.initSign(privateKey);
            byte[] bytes = input.getBytes();
            signatureGenerator.update(bytes);
            byte[] signoutput = signatureGenerator.sign();
            output = signoutput;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String signature = Global.BytesToString(output);
        System.out.println("sign:  " + signature);
         return signature;
    }
    public static boolean verify(Transaction in) {
        Signature signatureVerifier;
        try {
            signatureVerifier = Signature.getInstance("ECDSA", "BC");
            signatureVerifier.initVerify(in.from);
            signatureVerifier.update(in.getTransaction().getBytes());
             return signatureVerifier.verify(in.signature.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }
    public Block Send(int val, PublicKey you) {
        Block b;
        if (val <= funds) {
            funds -= val;
            Transaction a = new Transaction(this.publicKey, you, val);
            a.signature=Sign(a.getTransaction());
            Signature signatureVerifier = null;
            try {
                signatureVerifier = Signature.getInstance("ECDSA", "BC");
                signatureVerifier.initVerify(publicKey);
                signatureVerifier.update(a.getTransaction().getBytes());
                System.out.println("ELMFROD TRUE : "+signatureVerifier.verify(a.signature.getBytes()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (SignatureException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }


            b=Chain.add(a);
            System.out.println("---------------------------------------------++++++++++++++");
            Chain.save();
            transactionsSent.add(a);
            System.out.println((b==null)+"------------------------------");
            return b;
        } else {
            System.out.println("not enough funds");
            return null;
        }
    }
    public static boolean recieve(Transaction a) {
        if (verify(a)&&a.to==publicKey) {
            transactionsRecieved.add(a);
            return true;
        }
        return false;
    }


}
