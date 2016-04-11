package it.mahd.taxidriver.util;

import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by salem on 2/13/16.
 */
public class Encrypt {

    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final int min = 1000;

    public static String dec2enc(String data, String key){
        return encode(secure(data,key));
    }

    public static String enc2dec(String data, String key){
        return decode(secure(data, key));
    }

    public static int keyVirtual(){
        //min 1000, max 6000
        Random rand = new Random();
        return rand.nextInt(5001) + min;
    }

    public static String key(int key){
        return ((key * 4) + 4) + "";
    }

    public static String encode(String data){
        try{
            byte[] encData = data.getBytes(DEFAULT_ENCODING);
            String enc = Base64.encodeToString(encData, Base64.DEFAULT);
            return enc;
        }catch(UnsupportedEncodingException e){
            return null;
        }
    }

    public static String decode(String data){
        try {
            byte[] decData = Base64.decode(data, Base64.DEFAULT);
            String dec = new String(decData, "UTF-8");
            return dec;
        }catch(IOException e){
            return null;
        }
    }

    public static String secure(String message, String key){
        try{
            if (message==null || key==null ) return null;

            char[] kys = key.toCharArray();
            char[] msg = message.toCharArray();

            int ml = msg.length;
            int kl = kys.length;
            char[] newmsg = new char[ml];

            for (int i=0; i<ml; i++){
                newmsg[i] = (char)(msg[i]^kys[i%kl]);
            }
            msg=null; kys=null;
            return new String(newmsg);
        }catch(Exception e){
            return null;
        }
    }
}
