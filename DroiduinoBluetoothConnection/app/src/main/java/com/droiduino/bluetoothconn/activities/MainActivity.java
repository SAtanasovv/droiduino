package com.droiduino.bluetoothconn.activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.droiduino.bluetoothconn.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.droiduino.bluetoothconn.Utils.CONNECTING_STATUS;
import static com.droiduino.bluetoothconn.Utils.MESSAGE_AUTONOMOUS;
import static com.droiduino.bluetoothconn.Utils.MESSAGE_BACKWARD;
import static com.droiduino.bluetoothconn.Utils.MESSAGE_FORWARD;
import static com.droiduino.bluetoothconn.Utils.MESSAGE_LEFT;
import static com.droiduino.bluetoothconn.Utils.MESSAGE_MANUAL;
import static com.droiduino.bluetoothconn.Utils.MESSAGE_READ;
import static com.droiduino.bluetoothconn.Utils.MESSAGE_RIGHT;
import static com.droiduino.bluetoothconn.Utils.MESSAGE_STOP;

public class MainActivity extends AppCompatActivity {

    private         String                  deviceName  = null;
    private         String                  deviceAddress;
    public static   Handler                 handler;
    public static   BluetoothSocket         mmSocket;
    public static   ConnectedThread         connectedThread;
    public static   CreateConnectThread     createConnectThread;
    public          Button                  buttonConnect;
    public          Toolbar                 toolbar;
    public          ProgressBar             progressBar;
    public          TextView                textViewInfo;
    public          Button                  buttonForward;
    public          Button                  buttonBackward;
    public          Button                  buttonLeft;
    public          Button                  buttonRight;
    public          Button                  buttonStop;
    public          Button                  buttonAutonomous;
    public          Button                  buttonManual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

       init();
       initBluetooth();
    }

    private void init(){
        // UI Initialization
        buttonConnect       = findViewById(R.id.buttonConnect);
        toolbar             = findViewById(R.id.toolbar);
        progressBar         = findViewById(R.id.progressBar);
        textViewInfo        = findViewById(R.id.textViewInfo);
        buttonForward       = findViewById(R.id.buttonForward);
        buttonBackward      = findViewById(R.id.buttonBackward);
        buttonLeft          = findViewById(R.id.buttonLeft);
        buttonRight         = findViewById(R.id.buttonRight);
        buttonStop          = findViewById(R.id.buttonStop);
        buttonAutonomous    = findViewById(R.id.buttonAutonomous);
        buttonManual        = findViewById(R.id.buttonManual);

        buttonForward.setEnabled(false);
        buttonBackward.setEnabled(false);
        buttonLeft.setEnabled(false);
        buttonRight.setEnabled(false);
        buttonStop.setEnabled(false);
        buttonAutonomous.setEnabled(false);
        buttonManual.setEnabled(false);

        // Select Bluetooth Device
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          // Move to adapter list
          Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
          startActivity(intent);
            }
        });

        buttonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectedThread.write(MESSAGE_FORWARD);
            }
        });
        buttonBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectedThread.write(MESSAGE_BACKWARD);
            }
        });
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectedThread.write(MESSAGE_LEFT);
            }
        });
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectedThread.write(MESSAGE_RIGHT);
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectedThread.write(MESSAGE_STOP);
            }
        });
        buttonAutonomous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectedThread.write(MESSAGE_AUTONOMOUS);
            }
        });

        buttonManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectedThread.write(MESSAGE_MANUAL);
            }
        });
    }

    private void initBluetooth(){
        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null){
            // Get the device address to make BT Connection
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show progress and connection status
            toolbar.setSubtitle("Connecting to " + deviceName + "...");
            progressBar.setVisibility(View.VISIBLE);
            buttonConnect.setEnabled(false);

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();
        }
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                toolbar.setSubtitle("Connected to " + deviceName);
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                buttonForward.setEnabled(true);
                                buttonBackward .setEnabled(true);
                                buttonLeft.setEnabled(true);
                                buttonRight .setEnabled(true);
                                buttonStop.setEnabled(true);
                                buttonAutonomous.setEnabled(true);
                                buttonManual.setEnabled(true);
                                break;
                            case -1:
                                toolbar.setSubtitle("Device fails to connect");
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        textViewInfo.setText("Arduino Message : " + msg.obj.toString());
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();     // Cancel discovery because it otherwise slows down the connection.
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();   // Unable to connect; close the socket and return.
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket   mmSocket;
        private final InputStream       mmInStream;
        private final OutputStream      mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket            = socket;
            InputStream tmpIn   = null;
            OutputStream tmpOut = null;

            try {
                tmpIn   = socket.getInputStream();
                tmpOut  = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream  = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer   = new byte[1024];   // buffer store for the stream
            int bytes       = 0;                // bytes returned from read()
                                                // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /* Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.*/
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.e("Arduino Message",readMessage);
                        handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
