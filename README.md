# Ex.No:2 Develop an android application to implement the AIDL server and client app. The server app hosts a Bound Service and contains the logic to return random colours to its client.The client app calls the service and changes the button's colour within the Main activity.

## AIM:

To Develop an android application to implement the AIDL server and client app. The server app hosts a Bound Service and contains the logic to return random colours to its client.
The client app calls the service and changes the button's colour within the Main activity using AIDL interface in Android Studio.

## EQUIPMENTS REQUIRED:

Android Studio(Min.required Griaffe )

## ALGORITHM:

Step 1: Open Android Stdio and then click on File -> New -> New project.

Step 2: Then type the Application name as CSAIDL and click Next.

Step 3: Then select the Minimum SDK as shown below and click Next.

Step 4: Then select the Empty Activity and click Next. Finally click Finish.

Step 5: Design layout in activity_main.xml.

Step 6: Display message give in MainActivity file(client/server).

Step 7: Save and run the application.

## PROGRAM:

### Server Application Program:

#### RandomColorService.java:
```
package com.example.csaidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.graphics.Color;

import java.util.Random;

import com.example.aidlcommon.IRandomColorService;

public class RandomColorService extends Service {

    private final Random random = new Random();

    private final IRandomColorService.Stub binder =
            new IRandomColorService.Stub() {

        @Override
        public int getRandomColor() {

            int red = random.nextInt(256);
            int green = random.nextInt(256);
            int blue = random.nextInt(256);

            return Color.rgb(red, green, blue);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
```

#### AndroidManifest.xml
```
<service
    android:name=".RandomColorService"
    android:exported="true">

    <intent-filter>
        <action android:name=
            "com.example.csaidlserver.RANDOM_COLOR_SERVICE"/>
    </intent-filter>

</service>
```

### Client Application Program:

#### MainActivity.java:
```
package com.example.csaidlclient;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aidlcommon.IRandomColorService;

public class MainActivity extends Activity {

    private static final String TAG = "AIDLClient";

    private Button btnChangeColor;
    private TextView tvStatus;

    private IRandomColorService randomColorService;

    private boolean isBound = false;

    private final ServiceConnection connection =
            new ServiceConnection() {

        @Override
        public void onServiceConnected(
                ComponentName name, IBinder service) {

            randomColorService =
                    IRandomColorService.Stub.asInterface(service);

            isBound = true;

            tvStatus.setText("Service Connected");
            tvStatus.setTextColor(Color.GREEN);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            randomColorService = null;
            isBound = false;

            tvStatus.setText("Service Disconnected");
            tvStatus.setTextColor(Color.RED);
        }
    };

    private void bindToRandomColorService() {

        Intent intent = new Intent(
                "com.example.csaidlserver.RANDOM_COLOR_SERVICE"
        );

        intent.setPackage("com.example.csaidlserver");

        boolean result = bindService(
                intent,
                connection,
                BIND_AUTO_CREATE
        );

        if (!result) {
            tvStatus.setText("Service Disconnected");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnChangeColor = findViewById(R.id.btnChangeColor);
        tvStatus = findViewById(R.id.tvStatus);

        bindToRandomColorService();

        btnChangeColor.setOnClickListener(v -> {

            if (isBound && randomColorService != null) {

                try {
                    int randomColor =
                            randomColorService.getRandomColor();

                    Log.d(TAG, "Generated Color: " + randomColor);

                    String hexColor =
                            String.format("#%08X", randomColor);

                    btnChangeColor.setBackgroundTintList(
                            ColorStateList.valueOf(randomColor)
                    );

                    Toast.makeText(
                            MainActivity.this,
                            "Color: " + hexColor,
                            Toast.LENGTH_SHORT
                    ).show();

                } catch (RemoteException e) {
                    Log.e(TAG, "AIDL Error", e);
                }

            } else {
                Toast.makeText(
                        MainActivity.this,
                        "Service Not Connected",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}
```

#### activity_main.xml:
```
<?xml version="1.0" encoding="utf-8"?>

<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical"
    android:padding="20dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="AIDL Client Application"
        android:textSize="24sp"
        android:textStyle="bold"/>

    <TextView
        android:id="@+id/tvStatus"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:text="Connecting..."
        android:textSize="18sp"
        android:padding="20dp"/>

    <Button
        android:id="@+id/btnChangeColor"
        android:layout_width="match_parent"
        android:layout_height="80dp"
        android:text="CHANGE COLOR"/>

</LinearLayout>
```


## OUTPUT

![WhatsApp Image 2026-11-24 at 9.13.53 AM.jpeg](OUTPUT/WhatsApp%20Image%202026-11-24%20at%209.13.53%20AM.jpeg)

![WhatsApp Image 2026-11-24 at 9.23.00 AM.jpeg](OUTPUT/WhatsApp%20Image%202026-11-24%20at%209.23.00%20AM.jpeg)




## RESULT
Thus a Simple Android Application to create a AIDL interface and communicate the process between client and server using AIDL interface in Android Studio is developed and executed successfully.