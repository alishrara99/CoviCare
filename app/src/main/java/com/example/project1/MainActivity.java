package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;




public class MainActivity extends AppCompatActivity {
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private ChatUtils chatUtils;

    private ListView listMainChat;
    private EditText edCreateMessage;
    private Button btnSendMessage;
    private ImageButton imgbtnmain1;
    private ImageButton imgbtnmain2;
    private ImageButton imgbtnmain3;
    private ImageButton imgbtnmain4;
    private ImageButton imgbtnmain5;
    private ImageButton imgbtnmain6;
    private ImageButton imgbtnmain7;
    private ImageButton imgbtnmain8;
    private ImageButton imgbtnmain9;

    private ArrayAdapter<String> adapterMainChat;

    private final int LOCATION_PERMISSION_REQUEST = 101;
    private final int SELECT_DEVICE = 102;

    public static final int MESSAGE_STATE_CHANGED = 0;
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;

    public static final String DEVICE_NAME = "deviceName";
    public static final String TOAST = "toast";
    private String connectedDevice;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_STATE_CHANGED:
                    switch (message.arg1) {
                        case ChatUtils.STATE_NONE:
                            setState("Not Connected");
                            break;
                        case ChatUtils.STATE_LISTEN:
                            setState("Waiting..");
                            break;
                        case ChatUtils.STATE_CONNECTING:
                            setState("Connecting..");
                            break;
                        case ChatUtils.STATE_CONNECTED:
                            setState("Connected: " + connectedDevice);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] buffer1 = (byte[]) message.obj;
                    String outputBuffer = new String(buffer1);
                    adapterMainChat.add("Me: " + outputBuffer);
                    break;
                case MESSAGE_READ:
                    byte[] buffer = (byte[]) message.obj;
                    String inputBuffer = new String(buffer, 0, message.arg1);
                    adapterMainChat.add(connectedDevice + ": " + inputBuffer);

                    break;
                case MESSAGE_DEVICE_NAME:
                    connectedDevice = message.getData().getString(DEVICE_NAME);
                    Toast.makeText(context, connectedDevice, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(context, message.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void setState(CharSequence subTitle) {
        getSupportActionBar().setSubtitle(subTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setState("Not Connected");
        context = this;

        init();
        initBluetooth();
        chatUtils = new ChatUtils(context, handler);
    }

    private void init() {
        listMainChat = findViewById(R.id.list_conversation);
        edCreateMessage = findViewById(R.id.ed_enter_message);
        btnSendMessage = findViewById(R.id.btn_send_msg);
        imgbtnmain1 = findViewById(R.id.imgbtn_main_1);
        imgbtnmain2 = findViewById(R.id.imgbtn_main_2);
        imgbtnmain3 = findViewById(R.id.imgbtn_main_3);
        imgbtnmain4 = findViewById(R.id.imgbtn_main_4);
        imgbtnmain5 = findViewById(R.id.imgbtn_main_5);
        imgbtnmain6 = findViewById(R.id.imgbtn_main_6);
        imgbtnmain7 = findViewById(R.id.imgbtn_main_7);
        imgbtnmain8 = findViewById(R.id.imgbtn_main_8);
        imgbtnmain9 = findViewById(R.id.imgbtn_main_9);

        adapterMainChat = new ArrayAdapter<String>(context, R.layout.message_layout);
        listMainChat.setAdapter(adapterMainChat);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edCreateMessage.getText().toString();
                if (!message.isEmpty()) {
                    edCreateMessage.setText("");
                    chatUtils.write(message.getBytes());
                }
            }
        });

        imgbtnmain1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "I’m hungry, please bring in some food";
                chatUtils.write(message.getBytes());
            }
        });

        imgbtnmain2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "I’m thirsty, please bring me a cup of water";
                chatUtils.write(message.getBytes());
            }
        });

        imgbtnmain3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "I feel pain, maybe a painkiller pill would do the trick";
                chatUtils.write(message.getBytes());
            }
        });

        imgbtnmain4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "I need to go to the restroom, please clear the way for me";
                chatUtils.write(message.getBytes());
            }
        });

        imgbtnmain5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "I need my medicine, please bring it to me";
                chatUtils.write(message.getBytes());
            }
        });

        imgbtnmain6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "My condition is worsening, call for medical help please!";
                chatUtils.write(message.getBytes());
            }
        });

        imgbtnmain7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Yes";
                chatUtils.write(message.getBytes());
            }
        });

        imgbtnmain8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "No";
                chatUtils.write(message.getBytes());
            }
        });

        imgbtnmain9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "My Condition is Stable";
                chatUtils.write(message.getBytes());
            }
        });



    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Device Not Supported!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search_devices:
                checkPermissions();
                return true;
            case R.id.menu_enable_bluetooth:
                enableBluetooth();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(context, DeviceListActivity.class);
            startActivityForResult(intent, SELECT_DEVICE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK) {
            String address = data.getStringExtra("deviceAddress");
            chatUtils.connect(bluetoothAdapter.getRemoteDevice(address));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(context, DeviceListActivity.class);
                startActivityForResult(intent, SELECT_DEVICE);
            } else {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage("Location permission is required.\n Please grant")
                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkPermissions();
                            }
                        })
                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        }).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            Toast.makeText(context, "Enabled Bluetooth", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Bluetooth already enabled!", Toast.LENGTH_SHORT).show();
        }

        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoveryIntent);
        }
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatUtils != null) {
            chatUtils.stop();
        }
    }*/
}