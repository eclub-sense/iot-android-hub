package com.eclubprague.iot.android.driothub.cloud.sensors;

/**
 * Created by Dat on 28.7.2015.
 */
public interface WriteableSensor {

    public void writePacket(byte[] payload);
    public void createPayload();
    public byte[] encrypt(String data);

}
