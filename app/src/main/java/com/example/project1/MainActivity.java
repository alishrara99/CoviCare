package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



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
    private String MAIN_CHANNEL_ID;

    private final Handler delay = new Handler();

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_STATE_CHANGED:
                    switch (message.arg1) {
                        case ChatUtils.STATE_NONE:
                            setState("Not Connected");
                            greyoutWidgets();
                            break;
                        case ChatUtils.STATE_LISTEN:
                            setState("Not Connected");
                            greyoutWidgets();
                            break;
                        case ChatUtils.STATE_CONNECTING:
                            setState("Connecting..");
                            greyoutWidgets();
                            break;
                        case ChatUtils.STATE_CONNECTED:
                            setState("Connected: " + connectedDevice);
                            unGreyoutWidgets();
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] buffer1 = (byte[]) message.obj;
                    String outputBuffer = new String(buffer1);
                    String time = new SimpleDateFormat("hh:mm ", Locale.getDefault()).format(Calendar.getInstance().getTime());
                    adapterMainChat.add(time + "Me: " + outputBuffer);
                    sound_msg_sent();
                    break;
                case MESSAGE_READ:
                    byte[] buffer = (byte[]) message.obj;
                    String inputBuffer = new String(buffer, 0, message.arg1);
                    String time2 = new SimpleDateFormat("hh:mm ", Locale.getDefault()).format(Calendar.getInstance().getTime());
                    adapterMainChat.add(time2 + connectedDevice + ": " + inputBuffer);
                    sound_msg_received();
                    //addNotification(inputBuffer);
                    notification(inputBuffer);
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
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        MAIN_CHANNEL_ID = "MAIN_CHANNEL_ID";

        context = this;


        init();
        initBluetooth();
        chatUtils = new ChatUtils(context, handler);
        if (!(chatUtils.getState()==ChatUtils.STATE_CONNECTED))
        {
            chatUtils.setState(ChatUtils.STATE_NONE);
        }
        if (initBluetooth()== true && bluetoothAdapter.isEnabled()) {
            chatUtils.startListening();
        }
        createNotificationChannel();


    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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
                String message = "I feel pain, maybe a painkiller pill would help";
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

    private boolean initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Device Not Supported!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
               case R.id.menu_disconnect:
                if (initBluetooth()==true && chatUtils.getState()==ChatUtils.STATE_CONNECTED) {
                    chatUtils.connectionLost();
                }
                else
                    Toast.makeText(context, "Not Connected!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_search_devices:
                if (initBluetooth()==true) {
                    if (bluetoothAdapter.isEnabled() && !(chatUtils.getState()==ChatUtils.STATE_CONNECTED)) {
                        checkPermissions();
                    }
                    else if (bluetoothAdapter.isEnabled()){
                        Toast.makeText(context, "Disconnect Current Device First!", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "Enable Bluetooth First!", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(context, "Device Not Supported!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_enable_bluetooth:
                if (initBluetooth()==true) {
                    enableBluetooth();

                }
                else
                    Toast.makeText(context, "Device Not Supported!", Toast.LENGTH_SHORT).show();
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
            delay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    chatUtils.startListening();
                }
            }, 2500);
        }else{
            Toast.makeText(context, "Bluetooth already enabled!", Toast.LENGTH_SHORT).show();
        }

        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoveryIntent);
        }
    }

   /* private void scrollDown() {
        ScrollView scroller = findViewById(R.id.scroller);
        scroller.smoothScrollTo(0, listMainChat.getBottom());
    } */

    private void greyoutWidgets() {
        edCreateMessage.setClickable(false);
        btnSendMessage.setClickable(false);
        imgbtnmain1.setClickable(false);
        imgbtnmain2.setClickable(false);
        imgbtnmain3.setClickable(false);
        imgbtnmain4.setClickable(false);
        imgbtnmain5.setClickable(false);
        imgbtnmain6.setClickable(false);
        imgbtnmain7.setClickable(false);
        imgbtnmain8.setClickable(false);
        imgbtnmain9.setClickable(false);

        edCreateMessage.setEnabled(false);
        btnSendMessage.setEnabled(false);
        imgbtnmain1.setEnabled(false);
        imgbtnmain2.setEnabled(false);
        imgbtnmain3.setEnabled(false);
        imgbtnmain4.setEnabled(false);
        imgbtnmain5.setEnabled(false);
        imgbtnmain6.setEnabled(false);
        imgbtnmain7.setEnabled(false);
        imgbtnmain8.setEnabled(false);
        imgbtnmain9.setEnabled(false);


        imgbtnmain1.setAlpha((float) 0.66);
        imgbtnmain2.setAlpha((float) 0.66);
        imgbtnmain3.setAlpha((float) 0.66);
        imgbtnmain4.setAlpha((float) 0.66);
        imgbtnmain5.setAlpha((float) 0.66);
        imgbtnmain6.setAlpha((float) 0.66);
        imgbtnmain7.setAlpha((float) 0.66);
        imgbtnmain8.setAlpha((float) 0.66);
        imgbtnmain9.setAlpha((float) 0.66);
    }

    private void unGreyoutWidgets() {


        edCreateMessage.setClickable(true);
        btnSendMessage.setClickable(true);
        imgbtnmain1.setClickable(true);
        imgbtnmain2.setClickable(true);
        imgbtnmain3.setClickable(true);
        imgbtnmain4.setClickable(true);
        imgbtnmain5.setClickable(true);
        imgbtnmain6.setClickable(true);
        imgbtnmain7.setClickable(true);
        imgbtnmain8.setClickable(true);
        imgbtnmain9.setClickable(true);

        edCreateMessage.setEnabled(true);
        btnSendMessage.setEnabled(true);
        imgbtnmain1.setEnabled(true);
        imgbtnmain2.setEnabled(true);
        imgbtnmain3.setEnabled(true);
        imgbtnmain4.setEnabled(true);
        imgbtnmain5.setEnabled(true);
        imgbtnmain6.setEnabled(true);
        imgbtnmain7.setEnabled(true);
        imgbtnmain8.setEnabled(true);
        imgbtnmain9.setEnabled(true);

        imgbtnmain1.setAlpha((float) 1);
        imgbtnmain2.setAlpha((float) 1);
        imgbtnmain3.setAlpha((float) 1);
        imgbtnmain4.setAlpha((float) 1);
        imgbtnmain5.setAlpha((float) 1);
        imgbtnmain6.setAlpha((float) 1);
        imgbtnmain7.setAlpha((float) 1);
        imgbtnmain8.setAlpha((float) 1);
        imgbtnmain9.setAlpha((float) 1);



    }

    private void sound_msg_received(){
        final MediaPlayer sound_msg_received = MediaPlayer.create(this, R.raw.msg_received);
        sound_msg_received.start();
    }

    private void sound_msg_sent(){
        final MediaPlayer sound_msg_sent = MediaPlayer.create(this, R.raw.msg_sent);
        sound_msg_sent.start();
    }



    private void notification(String getString) {
        //Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder= new
                NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle(connectedDevice);
        builder.setContentText(getString);
        builder.setSmallIcon(R.mipmap.ic_covicare_round);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setPriority(2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(MAIN_CHANNEL_ID);
        }

        //Intent intent=new Intent(this, MainActivity.class);
        //PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //builder.setContentIntent(pendingIntent);
        //builder.setDefaults(Notification.DEFAULT_VIBRATE);
        //builder.setDefaults(Notification.DEFAULT_SOUND);


        NotificationManager notificationManager= (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mainChannel = new NotificationChannel
                    (MAIN_CHANNEL_ID,"Main Channel", NotificationManager.IMPORTANCE_HIGH);
            mainChannel.enableVibration(true);
            mainChannel.shouldVibrate();

        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(mainChannel);
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