package com.example.universal_ac_tv_remote.bluetooth;

public class BluetoothControlSingleton {
    private static BluetoothControl instance;

    public static void setInstance(BluetoothControl control) {
        instance = control;
    }

    public static BluetoothControl getInstance() {
        return instance;
    }
}
