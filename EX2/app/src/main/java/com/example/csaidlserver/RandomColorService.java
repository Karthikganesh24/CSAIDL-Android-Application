package com.example.csaidlserver;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.aidlcommon.IRandomColorService;

import java.util.Random;

public class RandomColorService extends Service {

    private final Random random = new Random();

    private final IRandomColorService.Stub binder = new IRandomColorService.Stub() {
        @Override
        public int getRandomColor() throws RemoteException {
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            return Color.rgb(r, g, b);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
