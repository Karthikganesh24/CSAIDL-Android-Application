package com.example.csaidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aidlcommon.IRandomColorService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CSAIDLClient";

    private IRandomColorService randomColorService;
    private boolean isBound = false;

    private TextView tvStatus;
    private Button btnChangeColor;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            randomColorService = IRandomColorService.Stub.asInterface(service);
            isBound = true;
            if (tvStatus != null) {
                tvStatus.setText(R.string.status_connected);
                tvStatus.setTextColor(Color.parseColor("#16A34A"));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            randomColorService = null;
            isBound = false;
            if (tvStatus != null) {
                tvStatus.setText(R.string.status_disconnected);
                tvStatus.setTextColor(Color.parseColor("#DC2626"));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvStatus = findViewById(R.id.tvStatus);
        btnChangeColor = findViewById(R.id.btnChangeColor);

        btnChangeColor.setOnClickListener(v -> {
            if (isBound && randomColorService != null) {
                try {
                    int randomColor = randomColorService.getRandomColor();
                    Log.d(TAG, "getRandomColor() returned integer: " + randomColor);

                    String hexColor = String.format("#%08X", randomColor);
                    Toast.makeText(MainActivity.this, "Color: " + hexColor, Toast.LENGTH_SHORT).show();

                    btnChangeColor.setBackgroundTintList(ColorStateList.valueOf(randomColor));
                    btnChangeColor.setBackgroundColor(randomColor);
                } catch (RemoteException e) {
                    Log.e(TAG, "Error calling getRandomColor() via AIDL", e);
                    tvStatus.setText(R.string.status_disconnected);
                    tvStatus.setTextColor(Color.parseColor("#DC2626"));
                    Toast.makeText(MainActivity.this, "Remote Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Service Disconnected. Attempting to connect...", Toast.LENGTH_SHORT).show();
                bindToRandomColorService();
            }
        });

        bindToRandomColorService();
    }

    private void bindToRandomColorService() {
        Intent intent = new Intent("com.example.csaidlserver.RANDOM_COLOR_SERVICE");
        intent.setPackage("com.example.csaidlserver");
        intent.setComponent(new ComponentName("com.example.csaidlserver", "com.example.csaidlserver.RandomColorService"));

        boolean success = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!success) {
            tvStatus.setText(R.string.status_disconnected);
            tvStatus.setTextColor(Color.parseColor("#DC2626"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
            randomColorService = null;
        }
    }
}
