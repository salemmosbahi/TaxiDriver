package it.mahd.taxidriver.util;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by salem on 3/11/16.
 */
public class SocketIO {
    Controllers conf = new Controllers();
    private static Socket socket;

    private SocketIO() {
        try{
            socket = IO.socket(conf.url);
        }catch(URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Socket getInstance() {
        if (socket == null) {
            new SocketIO();
        }
        return socket;
    }
}
