package com.example.user.bluecheck;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class BlueToothservice extends Service {

    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

    private Timer timer = new Timer();
    private android.os.Handler handler = new android.os.Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        tim();
        //  btAdapter.startDiscovery();
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver3, filter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("use", "new ");
            startMyOwnForeground();

        } else {
            Log.d("use", "old");
            startForeground(1, new Notification());

        }
    }

    private void startMyOwnForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.noti)
                    .setContentTitle("Blue app running in the backg")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);

        }

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();


            // It means the user has changed his bluetooth state.
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                if (btAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
                    // The user bluetooth is turning off yet, but it is not disabled yet.
                    return;
                }

                if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    //The user Bluetooth is turned off, start the main activity again
//                    Intent myService = new Intent(MainActivity.this, MyServiceActivity.class);
//                    //startService(myService);
//                    stopService(myService);
                    Intent i = new Intent(context, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    Log.d("card",btAdapter.getName());
                    Toast.makeText(context,"Bluetooth Disconnected",Toast.LENGTH_SHORT).show();

                    return;
                }
            }
        }
    };

    public final BroadcastReceiver mReceiver3 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();


            //Finding devices
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // Add the name and address to an array adapter to show in a ListView
                //    mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                Log.i("WORK: " , "now " + device.getName()+ "\t" +device.getAddress());


// for finding pixel 2xl address
                if(device.getAddress().equals("B4:F7:A1:22:36:BD")){
                    //  tim2();
                    //   btAdapter.cancelDiscovery();
                    Log.d("gh: " , "now " + device.getName());
                    Toast.makeText(context,"Device is nearby: " +device.getName(),Toast.LENGTH_SHORT).show();
                    //    startService(new Intent(context, MyServiceActivity.class));


                }
                else{
                 //   Toast.makeText(context,"Other devices nearby " +device.getName(),Toast.LENGTH_SHORT).show();
                    //   stopService(new Intent(context, MyServiceActivity.class));
                }
//For PIXEL ADDRESS
//                if(device.getAddress().equals("80:5A:04:2C:D4:30")){
//                    tim();
//                    //  tim2();
//                    //   btAdapter.cancelDiscovery();
//                    Log.d("gh: " , "now " + device.getName());
//                    Toast.makeText(context,"Device is nearby: " +device.getName(),Toast.LENGTH_SHORT).show();
//                    //    startService(new Intent(context, MyServiceActivity.class));
//
//
//                }
//                else{
//                 //   Toast.makeText(context,"Other devices nearby " +device.getName(),Toast.LENGTH_SHORT).show();
//                    //   stopService(new Intent(context, MyServiceActivity.class));
//                }



            }

            if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
              //  stopService(new Intent(context, MyServiceActivity.class));
            }


        }
    };

    public void tim(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        btAdapter.startDiscovery();
                        if (btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 5000);
                          //  startActivity(discoverableIntent);
                        }

                    }
                });
            }
        }, 0, 1000);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
