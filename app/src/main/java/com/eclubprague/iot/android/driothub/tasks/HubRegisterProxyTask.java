package com.eclubprague.iot.android.driothub.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by Dat on 3.8.2015.
 */
public class HubRegisterProxyTask extends AsyncTask<Hub, Void, Void> {



    @Override
    protected Void doInBackground(Hub... hubs) {


        try{

            connectWebSocket(hubs[0]);

            Log.e("connecting ws: ", hubs[0].toString());

        } catch(Exception e) {
            Log.e("HubRegisterProxyTask:", e.toString());
        }


        return null;
    }


    WebSocketClient mWebSocketClient;

    private void connectWebSocket(final Hub hub) {
        URI uri;
        try {
            uri = new URI("ws://192.168.201.222:8080/events");
        } catch (Exception e) {
            Log.e("URI:", e.toString());
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {



            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("onOpen:","wSocket opened");
                send(hub.toString());
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.e("onMessage:", message);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.e("onClose:","Websocket Closed");
            }

            @Override
            public void onError(Exception e) {
                System.out.println("Error");
            }
        };

        mWebSocketClient.connect();
    }
}
