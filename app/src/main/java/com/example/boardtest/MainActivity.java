package com.example.boardtest;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.serenegiant.common.UVCStruct;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.widget.UVCCameraTextureView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.SerialPort;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "MainActivity";
    private static final String ACTION_INTENT_RECEIVER = "com.serenegiant.widget.onSurfaceTextureAvailable";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static String tel = "15306160620";
    private TextView tv_backgrount;
    private VideoView vv_play;
    private LinearLayout ll_serialport;
    private LinearLayout ll_network;
    private LinearLayout ll_usbdevices;
    private LinearLayout ll_camera;
    private LinearLayout ll_mic;
    private LinearLayout ll_call;
    private LinearLayout ll_rtc;

    private static final int VIDEO_TEST = 0x01;
    private static final int NET_TEST = 0x02;
    private static final int SERIAL_TEST = 0x03;
    private static final int CAMERA_TEST = 0x04;
    private static final int USB_TEST = 0x05;
    private static final int MIC_TEST = 0x06;
    private static final int RTC_TEST = 0x07;
    private int state;


    private TextView tv_network_test;
    private TextView tv_net_info;
    private TextView tv_usb_info;
    private TextView tv_rtc_info;

    private Uri uri = null;

    private static final int USB_CONNECT = 0x16;
    private static final int USB_DISCONNECT = 0x17;
    private static final int SERIAL_RESULT = 0x18;
    private static final int PING_RESULT = 0x19;

    private static final int CALL_RESULT = 0x20;
    private static final int HUP_RESULT = 0x21;
    private static final int RTC_RESULT = 0x22;

    //串口
    private boolean ttyALL_exit = true;
    private List<String> serialports;
    public static final int BAUDRATE = 9600;
    protected SerialPort mSerialPort;
    protected InputStream mInputStream;
    protected OutputStream mOutputStream;
    private byte[] sinal_one = {1};
    private Map<String, TextView> tvs;

    private String getway;

    //USB设备相关
    private USBMonitor mUSBMonitor;
//    private USBBroadcastReceiver usbBroadcastReceiver;
//    private UsbManager usbManager;
//    private BroadcastReceiver receiver;
//    private IntentFilter intentFilter;

    //音频相关 录音，播放
    private MediaRecorder recorder;
    private MediaPlayer player;
    private File audioFile;
    private boolean isRecording = false;
    private RecordAmplitude recordAmplitude;
    private ProgressBar audioProgressBar;
    private TextView statusTextView;
    private Button startRecording;
    private Button stopRecording;
    private Button playRecording;
    private RecordManager rm = null;

    //摄像头相关
    private Map<UsbDevice, CameraResStruct> usbMap;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver receiver;

    //拨打电话相关
    private Button btn_call;
    private Button btn_hangup;
    private EditText et_tel;

    private final MyHandler mHandler = new MyHandler(this);

    public void onStartRecording(View view) {
//        recorder = new MediaRecorder();
//        //recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        //recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//       // recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//        File path = new File(Environment.getExternalStorageDirectory() + "/ceshi");
//        path.mkdirs();
//        try {
//            audioFile = File.createTempFile("recording", ".3gp", path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //recorder.setOutputFile(audioFile.getAbsolutePath());
//
//        try {
//            recorder.prepare();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//       // recorder.start();
//
//        isRecording = true;
//        recordAmplitude = new RecordAmplitude();
//        recordAmplitude.execute();

        rm.startRecord();
        statusTextView.setText("Recording");

        playRecording.setEnabled(false);
        stopRecording.setEnabled(true);
        startRecording.setEnabled(false);
    }

    public void onStopRecording(View view) {
        audioProgressBar.setProgress(0);

//        isRecording = false;
//        recordAmplitude.cancel(true);
//        recorder.stop();
//        recorder.release();
        rm.stopRecord();

        player = new MediaPlayer();
        player.setOnCompletionListener(this);

        try {
            player.setDataSource(Environment.getExternalStorageDirectory() + "/ceshi");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        statusTextView.setText("Ready to Play");
        playRecording.setEnabled(true);
        stopRecording.setEnabled(false);
        startRecording.setEnabled(true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        statusTextView.setText("Ready");

        playRecording.setEnabled(true);
        stopRecording.setEnabled(false);
        startRecording.setEnabled(true);
    }

    public void onPlayRecording(View view) {
        audioProgressBar.setProgress(0);

        player.start();
        statusTextView.setText("Playing");

        playRecording.setEnabled(false);
        stopRecording.setEnabled(false);
        startRecording.setEnabled(false);
    }

    public void onStartScreenTest(View view) {
        DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDevicePolicyManager.lockNow();
    }

    public void onClose(View view) {
        finish();
    }

    public void onStartTestWiFi(View view) {
        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
    }

    public void onStartTestRTC(View view) {
        tv_rtc_info.setText("");
        state = RTC_TEST;

        vv_play.stopPlayback();
        vv_play.setVisibility(View.GONE);
        ll_serialport.setVisibility(View.GONE);
        ll_usbdevices.setVisibility(View.GONE);
        ll_camera.setVisibility(View.GONE);
        ll_mic.setVisibility(View.GONE);
        ll_call.setVisibility(View.GONE);
        ll_network.setVisibility(View.GONE);

        ll_rtc.setVisibility(View.VISIBLE);

        new Thread(() -> testRtc()).start();
    }


    class MyHandler extends Handler {
        private String str_res;
        private WeakReference<MainActivity> mActivityRef;

        public MyHandler(MainActivity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivityRef.get();
            switch (msg.what) {
                case HUP_RESULT:
                    String res_hup = (String) msg.obj;
                    if (res_hup.contains("OK")) {
                        Toast.makeText(activity, "电话挂断成功！", Toast.LENGTH_SHORT).show();
                    } else if (res_hup.contains("ERROR")) {
                        Toast.makeText(activity, "电话挂断失败！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CALL_RESULT:
                    String res_call = (String) msg.obj;
                    if (res_call.contains("OK")) {
                        Toast.makeText(activity, "电话拨打成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "电话拨打失败！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case USB_CONNECT:
                case USB_DISCONNECT:
                    if (state == USB_TEST) {
                        onStartUSBTest(null);
                    }
                    break;
                case SERIAL_RESULT:
                    String name = (String) msg.obj;
                    TextView tv = tvs.get(name);
                    int size = Integer.parseInt(tv.getText().toString()) + msg.arg1;
                    tv.setText(String.valueOf(size));
                    break;
                case PING_RESULT:
                    str_res = activity.tv_network_test.getText().toString();
                    activity.tv_network_test.setText("返回：" + msg.obj.toString() + "\n" + str_res);
                    break;
                case RTC_RESULT:
                    str_res = activity.tv_rtc_info.getText().toString();
                    activity.tv_rtc_info.setText(msg.obj.toString() + "\n" + str_res);
                    break;
                default:
                    break;

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initDatas();

        //在这里动态申请文件读写权限
        verifyStoragePermissions();
        //====================>>>>>>>>>>>>
    }

    //在大于23的android版本中,文件读写需要动态申请权限
    private void verifyStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                    (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                    (this.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
            } else {
                Log.d(TAG, "已有权限,不需要再申请");
                //mHandler.sendEmptyMessage(MyHandler.PERMISSION_ALLOW);
                //doNext();
            }
        }
    }


    private void initDatas() {
        serialports = new ArrayList<>();
        tvs = new HashMap<>();

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.hardware.usb.action.USB_STATE");
//        usbBroadcastReceiver = new USBBroadcastReceiver();
//        registerReceiver(usbBroadcastReceiver, intentFilter);

//        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
//        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
//        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        intentFilter.addAction(MyUsbManager.ACTION_USB_STATE);
//        receiver = new USBReceiver();
//        registerReceiver(receiver, intentFilter);

        uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dazhong);
        initVideo();
        vv_play.start();

//        boolean hasUsbHost = getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST);
//        boolean hasUsbAccessory = getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY);
//        addText("hasUsbHost : " + hasUsbHost);
//        addText("hasUsbAccessory : " + hasUsbAccessory);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒 E");
        if (AExecuteAsRoot.canRunRootCommands()) {
            setTitle("     SU OK !" + "    当前时间：" + dateFormat.format(date) + "  适用版本最低android 5.1");
        } else {
            setTitle("     SU 不OK!" + "    当前时间：" + dateFormat.format(date) + "  适用版本最低android 5.1");
        }

        audioProgressBar.setMax(100);
        state = 0;


        //摄像头相关初始化
        usbMap = new HashMap<>();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        receiver = new LocalReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INTENT_RECEIVER);
        localBroadcastManager.registerReceiver(receiver, filter);
    }

    private void initViews() {
        vv_play = findViewById(R.id.vv_testVideo);
        tv_backgrount = findViewById(R.id.tv_backgrount);
        ll_serialport = findViewById(R.id.ll_serialport);
        ll_network = findViewById(R.id.ll_network);
        ll_usbdevices = findViewById(R.id.ll_usbdevices);
        ll_camera = findViewById(R.id.ll_camera);
        ll_mic = findViewById(R.id.ll_mic);
        ll_call = findViewById(R.id.ll_call);
        ll_rtc = findViewById(R.id.ll_rtc);

        tv_network_test = findViewById(R.id.tv_network_test);
        tv_net_info = findViewById(R.id.tv_net_info);
        tv_usb_info = findViewById(R.id.tv_usb_info);
        tv_rtc_info = findViewById(R.id.tv_rtc_info);

        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);

        audioProgressBar = findViewById(R.id.progress_bar_h);
        statusTextView = findViewById(R.id.StatusTextView);
        stopRecording = findViewById(R.id.StopRecording);
        startRecording = findViewById(R.id.StartRecording);
        playRecording = findViewById(R.id.PlayRecording);

        btn_call = findViewById(R.id.btn_call);
        btn_hangup = findViewById(R.id.btn_hangup);
        et_tel = findViewById(R.id.et_tel);
    }

    private void initVideo() {
        vv_play.setVideoURI(uri);
        vv_play.requestFocus();
        vv_play.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.start();
            }
        });

        vv_play.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // TODO Auto-generated method stub
                vv_play.start();
                vv_play.seekTo(0);
                return false;
            }
        });
    }


    private void createSerialPortLayout() {
        LinearLayout ll_root = findViewById(R.id.ll_serialport);
        //建立三个竖方向排列的LinearLayout
        LinearLayout ll_title = new LinearLayout(this);
        ll_title.setOrientation(LinearLayout.HORIZONTAL);
        ll_root.addView(ll_title);

        //=================================================//
        final Button btn = new Button(this);

        btn.setText("全部开始");
        btn.setTextSize(28);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.gravity = Gravity.BOTTOM;
        ll_title.addView(btn, llp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttyALL_exit) {
                    ttyALL_exit = false;
                    btn.setText("全部停止");
                    openSerialPort();
                } else {
                    ttyALL_exit = true;
                    btn.setText("全部开始");
                }
            }
        });

        TextView tv_send = new TextView(this);
        tv_send.setText("发出的字节");
        tv_send.setBackgroundColor(Color.BLUE);
        tv_send.setGravity(Gravity.CENTER);
        tv_send.setTextSize(28);
        llp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.gravity = Gravity.BOTTOM;
        llp.weight = 1;
        ll_title.addView(tv_send, llp);

        TextView tv_rece = new TextView(this);
        tv_rece.setText("接收的字节");
        tv_rece.setGravity(Gravity.CENTER);
        tv_rece.setTextSize(28);
        llp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.gravity = Gravity.BOTTOM;
        llp.weight = 1;
        tv_rece.setBackgroundColor(Color.RED);
        ll_title.addView(tv_rece, llp);

        File file = new File("/dev");
        File[] listFiles = file.listFiles();
        for (File file1 : listFiles) {
            String ttyName = file1.getName();
            //名字大于3个字母，包含tty，第四个字母不是数字
            if ((ttyName.length() > 3) && ((ttyName.contains("ttymxc")) || (ttyName.contains("ttyS")) || (ttyName.contains("ttyswk")) || (ttyName.contains("ttysWK")))) {
                //Log.d(TAG, "=name=>>" + ttyName);
                serialports.add(ttyName);

                LinearLayout ll_content = new LinearLayout(this);
                ll_content.setOrientation(LinearLayout.HORIZONTAL);
                ll_root.addView(ll_content);

                TextView tvName = new TextView(this);
                tvName.setText(ttyName);
                tvName.setTextSize(28);
                tvName.setPadding(30, 0, 0, 0);
                tvName.setGravity(Gravity.LEFT);
                llp = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
                ll_content.addView(tvName, llp);

                TextView tvSend = new TextView(this);
                tvs.put(ttyName + "Send", tvSend);
                tvSend.setText("0");
                tvSend.setTextSize(28);
                tvSend.setPadding(10, 0, 0, 0);
                tvSend.setGravity(Gravity.LEFT);
                llp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                llp.weight = 1;
                ll_content.addView(tvSend, llp);

                TextView tvRece = new TextView(this);
                tvs.put(ttyName + "Rece", tvRece);
                tvRece.setText("0");
                tvRece.setTextSize(28);
                tvRece.setPadding(30, 0, 0, 0);
                tvRece.setGravity(Gravity.LEFT);
                llp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                llp.weight = 1;
                ll_content.addView(tvRece, llp);
            }
        }
    }

    private void openSerialPort() {
        for (String name : serialports) {
            SerialPortUtil spu_one = new SerialPortUtil(name, BAUDRATE);
            SendRunnable sendRunnable = new SendRunnable();
            sendRunnable.setName(name);
            sendRunnable.setSpu_one(spu_one);
            //将所有接收发送框清零
            resetTvs();
            new Thread(sendRunnable).start();

            spu_one.setOnDataReceiveListener((name1, buffer, size) -> {
                Message msg = Message.obtain();
                msg.what = SERIAL_RESULT;
                msg.obj = name1 + "Rece";
                msg.arg1 = size;
                mHandler.sendMessage(msg);
            });
        }
    }

    //将所有接收发送框清零
    private void resetTvs() {
        //第一种：(效率高)
        //System.out.println("第一种方法：");
        Iterator iter = tvs.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            TextView tv = (TextView) entry.getValue();
            //System.out.println("键:"+key+"<==>"+"值:"+val);
            tv.setText("0");
        }
    }

    private String getGateWay() {
        String[] arr;
        try {
            Process process = Runtime.getRuntime().exec("ip route list table 0");
            String data = null;
            BufferedReader ie = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String string = in.readLine();

            arr = string.split("\\s+");
            return arr[2];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    private void getNetInfo() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        String addr = intToInetAddress(dhcpInfo.gateway).getHostAddress();
        addr = "";
    }

    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }


    private class NetPing extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //String s = Ping("www.baidu.com");
            String s = Ping("192.168.1.1");
            Log.i("ping", s);
            return s;
        }
    }

    public String Ping(String str) {
        String resault = "";
        Process p;
        try {
            //ping -c 3 -w 100  中  ，-c 是指ping的次数 3是指ping 3次 ，-w 100  以秒为单位指定超时间隔，是指超时时间为100秒
            p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + str);
            int status = p.waitFor();

            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
                Message msg = Message.obtain();
                msg.what = PING_RESULT;
                msg.obj = line;
                mHandler.sendMessage(msg);
            }
            System.out.println("Return ============" + buffer.toString());

            if (status == 0) {
                resault = "success";
            } else {
                resault = "faild";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resault;
    }

    private class SendRunnable implements Runnable {
        private SerialPortUtil spu_one;
        private String name;

        public void setSpu_one(SerialPortUtil spu_one) {
            this.spu_one = spu_one;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (!ttyALL_exit) {
                spu_one.sendBuffer(sinal_one);
                Message msg = Message.obtain();
                msg.what = SERIAL_RESULT;
                msg.obj = name + "Send";
                msg.arg1 = sinal_one.length;
                mHandler.sendMessage(msg);

                SystemClock.sleep(500);
            }

        }
    }

    public boolean testRtc() {
        //Log.d(TAG, "into testRtc");
        String result = "";
        while (state == RTC_TEST) {
            ArrayList<String> commandList = new ArrayList<String>();
            commandList.add("busybox hwclock -w");
            ExecuteAsRoot executeAsRoot = new ExecuteAsRoot(commandList);
            executeAsRoot.execute();

            SystemClock.sleep(100);

            commandList.clear();
            commandList.add("busybox hwclock -r");
            executeAsRoot = new ExecuteAsRoot(commandList);
            result = executeAsRoot.execute();

            Message msg = Message.obtain();
            msg.what = RTC_RESULT;
            msg.obj = result;
            mHandler.sendMessage(msg);

            SystemClock.sleep(1000);
        }

        //Log.d(TAG, "out testRtc ==>> " );
        return false;
    }

    public boolean isAvailableByPing(String ip) {
        if ((ip == null) || (ip.length() <= 0)) {
            ip = "192.168.1.1";
        }
        Runtime runtime = Runtime.getRuntime();
        Process ipProcess = null;
        BufferedReader bufferedReader = null;
        String line = null;
        try {
            //-c 后边跟随的是重复的次数，-w后边跟随的是超时的时间，单位是秒，不是毫秒，要不然也不会anr了
            //-s 后面跟随的是包的大小
            ipProcess = runtime.exec("ping -s 30720 " + ip);
            bufferedReader = new BufferedReader(new InputStreamReader(ipProcess.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                Log.d("ping", "=line=>>" + line);
                Message msg = Message.obtain();
                msg.what = PING_RESULT;
                msg.obj = line;
                mHandler.sendMessage(msg);
            }
            int exitValue = ipProcess.waitFor();
            Log.d("ping", "Process:" + exitValue);
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //在结束的时候应该对资源进行回收
            if (ipProcess != null) {
                ipProcess.destroy();
            }
            runtime.gc();
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUSBMonitor.register();
    }

    @Override
    protected void onStop() {
        mUSBMonitor.unregister();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }

        localBroadcastManager.unregisterReceiver(receiver);

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onStartVideoTest(null);
    }

    public void onStartVideoTest(View view) {
        state = VIDEO_TEST;

        ll_serialport.setVisibility(View.GONE);
        ll_network.setVisibility(View.GONE);
        ll_usbdevices.setVisibility(View.GONE);
        ll_camera.setVisibility(View.GONE);
        ll_mic.setVisibility(View.GONE);
        ll_call.setVisibility(View.GONE);
        ll_rtc.setVisibility(View.GONE);

        vv_play.setVisibility(View.VISIBLE);
        vv_play.start();
        vv_play.seekTo(0);
    }

    public void onStartNetworkTest(View view) {
        tv_network_test.setText("");
        state = NET_TEST;

        vv_play.stopPlayback();
        vv_play.setVisibility(View.GONE);
        ll_serialport.setVisibility(View.GONE);
        ll_usbdevices.setVisibility(View.GONE);
        ll_camera.setVisibility(View.GONE);
        ll_mic.setVisibility(View.GONE);
        ll_call.setVisibility(View.GONE);
        ll_rtc.setVisibility(View.GONE);

        ll_network.setVisibility(View.VISIBLE);

        getway = getGateWay();
        tv_net_info.setText(tv_net_info.getText().toString() + getway);

        //      new NetPing().execute();
        new Thread(() -> isAvailableByPing(getway)).start();
    }

    public void onStartSerialPortTest(View view) {
        state = SERIAL_TEST;

        vv_play.stopPlayback();
        vv_play.setVisibility(View.GONE);
        ll_network.setVisibility(View.GONE);
        ll_usbdevices.setVisibility(View.GONE);
        ll_camera.setVisibility(View.GONE);
        ll_mic.setVisibility(View.GONE);
        ll_call.setVisibility(View.GONE);
        ll_rtc.setVisibility(View.GONE);

        ll_serialport.setVisibility(View.VISIBLE);
        ll_serialport.removeAllViews();

        createSerialPortLayout();
    }

    public void onUpdateUSBDevices(View view) {
        List<UsbDevice> devices = mUSBMonitor.getDeviceList();
        if (devices == null) {
            Toast.makeText(this, "没有检测到usb设备", Toast.LENGTH_SHORT).show();
            tv_usb_info.setText("没有检测到usb设备");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (UsbDevice device : devices) {
            builder.append("设备名：" + device.getProductName());
            builder.append("\n\n");
        }
        tv_usb_info.setText(builder.toString());
    }

    public void onStartUSBTest(View view) {
        state = USB_TEST;

        vv_play.setVisibility(View.GONE);
        ll_network.setVisibility(View.GONE);
        ll_serialport.setVisibility(View.GONE);
        ll_camera.setVisibility(View.GONE);
        ll_mic.setVisibility(View.GONE);
        ll_call.setVisibility(View.GONE);
        ll_rtc.setVisibility(View.GONE);

        ll_usbdevices.setVisibility(View.VISIBLE);

        //tv_usb_info.setText("USB Test");
        SystemClock.sleep(1000);

        List<UsbDevice> devices = mUSBMonitor.getDeviceList();
        if (devices.isEmpty()) {
            Toast.makeText(this, "没有检测到usb设备", Toast.LENGTH_SHORT).show();
            tv_usb_info.setText("没有检测到USB设备");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (UsbDevice device : devices) {
            builder.append("设备名：" + device.getProductName());
            builder.append("\n\n");
        }
        tv_usb_info.setText(builder.toString());

    }

    public void onStartCameraTest(View view) {
        state = CAMERA_TEST;

        vv_play.setVisibility(View.GONE);
        ll_network.setVisibility(View.GONE);
        ll_usbdevices.setVisibility(View.GONE);
        ll_serialport.setVisibility(View.GONE);
        ll_mic.setVisibility(View.GONE);
        ll_call.setVisibility(View.GONE);
        ll_rtc.setVisibility(View.GONE);

        ll_camera.setVisibility(View.VISIBLE);

        List<UsbDevice> devices = mUSBMonitor.getDeviceList();
        if (devices == null) {
            Toast.makeText(this, "没有检测到usb设备", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usbMap.size() != 0) {
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            for (UsbDevice device : devices) {
                buildSurfaceTexture(this, device, ll);
            }
            ll_camera.addView(ll);
        }
    }

    private void buildSurfaceTexture(Context context, UsbDevice device, LinearLayout ll) {
        CameraResStruct cts = usbMap.get(device);
        if (cts != null) {
            UVCCameraTextureView mUVCCameraView = cts.getmUVCCameraView();
            if (mUVCCameraView == null) {
                LinearLayout ll_temp = (LinearLayout) View.inflate(context, R.layout.cameraview_layout, null);
                mUVCCameraView = ll_temp.findViewById(R.id.camera_view);
                mUVCCameraView.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float) UVCCamera.DEFAULT_PREVIEW_HEIGHT);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(250, 200);
                llp.leftMargin = 10;
                ll.addView(ll_temp, llp);
                cts.setmUVCCameraView(mUVCCameraView);
            }
        }

    }

    private void openCameraDevice(final USBMonitor.UsbControlBlock ctrlBlock, UVCCameraTextureView mUVCCameraView) {
        new Thread(() -> {
            final UVCCamera camera = new UVCCamera();
            try {
                camera.open(ctrlBlock);
            } catch (Exception e) {
                return;
            }


            Log.d(TAG, "camera.open:" + ctrlBlock);
            try {
                camera.setPreviewSize(
                        UVCCamera.DEFAULT_PREVIEW_WIDTH,
                        UVCCamera.DEFAULT_PREVIEW_HEIGHT,
                        UVCCamera.FRAME_FORMAT_MJPEG, 0.5f);
            } catch (final IllegalArgumentException e) {
                // fallback to YUV mode
                try {
                    camera.setPreviewSize(
                            UVCCamera.DEFAULT_PREVIEW_WIDTH,
                            UVCCamera.DEFAULT_PREVIEW_HEIGHT,
                            UVCCamera.DEFAULT_PREVIEW_MODE);
                } catch (final IllegalArgumentException e1) {
                    camera.destroy();
                    return;
                }
            }

            final SurfaceTexture st = mUVCCameraView.getSurfaceTexture();
            if (st != null) {
                Surface mPreviewSurface = new Surface(st);
                camera.setPreviewDisplay(mPreviewSurface);
                camera.startPreview();
            }
        }).start();
    }

    public void onStartTestCall(View view) {
        vv_play.setVisibility(View.GONE);
        ll_network.setVisibility(View.GONE);
        ll_usbdevices.setVisibility(View.GONE);
        ll_serialport.setVisibility(View.GONE);
        ll_camera.setVisibility(View.GONE);
        ll_mic.setVisibility(View.GONE);

        ll_call.setVisibility(View.VISIBLE);
    }


    public void onHungUP(View view) {
        Toast.makeText(this, "挂断指令已发出，请耐心等候！", Toast.LENGTH_SHORT).show();
        //开始挂断电话
        String telString = "ATH0";
        SerialPortUtil spu_hun = new SerialPortUtil("ttyUSB2", 115200);
        spu_hun.sendBuffer(telString.getBytes());
        spu_hun.setOnDataReceiveListener((name1, buffer, size) -> {
            String buf = null;
            try {
                buf = new String(buffer, "UTF-8");
                //Log.d(TAG, "=call result=>>" + name1 + "=buffer=>>" + buf);
                Message msg = Message.obtain();
                msg.what = HUP_RESULT;
                msg.obj = buf;
                mHandler.sendMessage(msg);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

    public void onCallSerial(View view) {
        Toast.makeText(this, "拨号指令已发出，请耐心等候！", Toast.LENGTH_SHORT).show();
        //15秒内不让call
        btn_call.setEnabled(false);
        btn_hangup.setEnabled(true);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    btn_call.setEnabled(true);
                });
            }
        };
        timer.schedule(timerTask, 15 * 1000);
        //开始拨打电话
        tel = et_tel.getText().toString();
        String telString = "ATD" + tel + ";";
        SerialPortUtil spu_call = new SerialPortUtil("ttyUSB2", 115200);
        spu_call.sendBuffer(telString.getBytes());
        spu_call.setOnDataReceiveListener((name1, buffer, size) -> {
            String buf = null;
            try {
                buf = new String(buffer, "UTF-8");
                //Log.d(TAG, "=call result=>>" + name1 + "=buffer=>>" + buf);
                Message msg = Message.obtain();
                msg.what = CALL_RESULT;
                msg.obj = buf;
                mHandler.sendMessage(msg);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

    public void onCall(View view) {
        vv_play.setVisibility(View.GONE);
        ll_network.setVisibility(View.GONE);
        ll_usbdevices.setVisibility(View.GONE);
        ll_serialport.setVisibility(View.GONE);
        ll_camera.setVisibility(View.GONE);
        ll_mic.setVisibility(View.GONE);

        ll_call.setVisibility(View.VISIBLE);


        //测试板子用的是串口的atd命令，不用下面的方式

        //callPhone("15306160620");
        // 检查是否获得了权限（Android6.0运行时权限）
//        if (ContextCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            // 没有获得授权，申请授权
//            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                    Manifest.permission.CALL_PHONE)) {
//                // 返回值：
////                          如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
////                          如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
////                          如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
//                // 弹窗需要解释为何需要该权限，再次请求授权
//                Toast.makeText(MainActivity.this, "请授权！", Toast.LENGTH_LONG).show();
//
//                // 帮跳转到该应用的设置界面，让用户手动授权
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("package", getPackageName(), null);
//                intent.setData(uri);
//                startActivity(intent);
//            } else {
//                // 不需要解释为何需要该权限，直接请求授权
//                ActivityCompat.requestPermissions(MainActivity.this,
//                        new String[]{Manifest.permission.CALL_PHONE},
//                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
//            }
//        } else {
//            // 已经获得授权，可以打电话
//            callPhone(tel);
//        }
    }

    /**
     * 测试板子用的是串口打电话，此方法不用
     * <p>
     * 拨打电话（直接拨打电话）
     *
     * @param phoneNum 电话号码
     */
    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }


    public void onStartMicTest(View view) {
        Log.d(TAG, "onStartMicTest: ");
        state = MIC_TEST;

        vv_play.stopPlayback();
        vv_play.setVisibility(View.GONE);
        ll_network.setVisibility(View.GONE);
        ll_usbdevices.setVisibility(View.GONE);
        ll_serialport.setVisibility(View.GONE);
        ll_camera.setVisibility(View.GONE);
        ll_call.setVisibility(View.GONE);
        ll_rtc.setVisibility(View.GONE);

        ll_mic.setVisibility(View.VISIBLE);
        playRecording.setEnabled(true);
        stopRecording.setEnabled(true);
        startRecording.setEnabled(true);
        statusTextView.setText("Ready");

        if (rm == null){
            File path = new File(Environment.getExternalStorageDirectory() + "/ceshi");
            rm = new RecordManager(path,audioProgressBar);
        }
    }

    private void saveConfig(String pid, int productId) {
        int pidL = getPid("PidL");
        int pidR = getPid("PidR");
        if ((pid.equals("PidL")) && (getPid("PidR") == productId)) {
            saveConfig("PidR", 0);
        } else if ((pid.equals("PidR")) && (getPid("PidL") == productId)) {
            saveConfig("PidL", 0);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(pid, productId);
        editor.commit();
    }

    private int getPid(String pid) {
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        return sharedPreferences.getInt(pid, -1);
    }

    private TextView tv2;

    private void createToucher(List<UsbDevice> devices) {
        //布局参数.
        LayoutParams params;
        //实例化的WindowManager.
        WindowManager windowManager;
        params = new LayoutParams();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        params.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.CENTER;
        params.width = 600;
        params.height = 500;

        RelativeLayout rly = new RelativeLayout(this);
        rly.setBackgroundColor(Color.WHITE);
        TextView tvTemp = null;
        Button btnLTemp = null;
        Button btnRTemp = null;
        for (UsbDevice device : devices) {
            TextView tv1 = new TextView(this);
            tv1.setText("设备名：" + device.getProductName() + " 设备ID：" + device.getProductId());
            tv1.setId(View.generateViewId());
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            rlp.leftMargin = 20;
            rlp.topMargin = 20;
            if (tvTemp != null) {
                rlp.addRule(RelativeLayout.BELOW, tvTemp.getId());
            }
            rly.addView(tv1, rlp);
            //加button
            Button btnL = new Button(this);
            btnL.setId(View.generateViewId());
            btnL.setBackgroundColor(Color.GREEN);
            btnL.setText("设置成左边");
            rlp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.RIGHT_OF, tv1.getId());
            if (tvTemp != null) {
                rlp.addRule(RelativeLayout.BELOW, tvTemp.getId());
            }
            if (btnLTemp != null) {
                rlp.addRule(RelativeLayout.ALIGN_LEFT, btnLTemp.getId());
            } else {
                rlp.leftMargin = 20;
            }
            btnL.setOnClickListener(v -> {
                saveConfig("PidL", device.getProductId());
                tv2.setText("左边ID：" + getPid("PidL") + " 右边ID：" + getPid("PidR"));
            });
            rly.addView(btnL, rlp);

            Button btnR = new Button(this);
            btnR.setBackgroundColor(Color.GREEN);
            btnR.setId(View.generateViewId());
            btnR.setText("设置成右边");
            rlp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.RIGHT_OF, btnL.getId());
            if (tvTemp != null) {
                rlp.addRule(RelativeLayout.BELOW, tvTemp.getId());
            }
            if (btnRTemp != null) {
                rlp.addRule(RelativeLayout.ALIGN_LEFT, btnRTemp.getId());
            }
            btnR.setOnClickListener(v -> {
                saveConfig("PidR", device.getProductId());
                tv2.setText("左边ID：" + getPid("PidL") + " 右边ID：" + getPid("PidR"));
            });
            rly.addView(btnR, rlp);

            Button btn_Open = new Button(this);
            btn_Open.setBackgroundColor(Color.GREEN);
            btn_Open.setId(View.generateViewId());
            btn_Open.setText("打开摄像头");
            rlp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.RIGHT_OF, btnR.getId());
            if (tvTemp != null) {
                rlp.addRule(RelativeLayout.BELOW, tvTemp.getId());
            }
            btn_Open.setOnClickListener(v -> {
//                if (device.getProductId() == getPid("PidL")) {
//                    if (mLeftControlBlock == null) {
//                        mUSBMonitor.requestPermission(device);
//                    } else {
//                        openCameraDevice(device, mLeftControlBlock);
//                    }
//                }
//                if (device.getProductId() == getPid("PidR")) {
//                    if (mRightControlBlock == null) {
//                        mUSBMonitor.requestPermission(device);
//                    } else {
//                        openCameraDevice(device, mRightControlBlock);
//                    }
//                }
            });
            rly.addView(btn_Open, rlp);

            tvTemp = tv1;
            btnLTemp = btnL;
            btnRTemp = btnR;
        }

        Button btn_close = new Button(this);
        btn_close.setBackgroundColor(Color.RED);
        btn_close.setId(View.generateViewId());
        btn_close.setText("关闭摄像头");
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlp.topMargin = 20;
        if (tvTemp != null) {
            rlp.addRule(RelativeLayout.BELOW, tvTemp.getId());
        }
        btn_close.setOnClickListener(v -> stopCamera());
        rly.addView(btn_close, rlp);

        tv2 = new TextView(this);
        tv2.setText("左边ID：" + getPid("PidL") + " 右边ID：" + getPid("PidR"));
        tv2.setId(View.generateViewId());
        tv2.setTextSize(40);
        rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlp.topMargin = 20;
        if (tvTemp != null) {
            rlp.addRule(RelativeLayout.BELOW, btn_close.getId());
        }
        rly.addView(tv2, rlp);

        windowManager.addView(rly, params);
    }

    public void stopCamera() {
//        synchronized (mSync) {
//            if (mUVCCameraL != null) {
//                try {
//                    mUVCCameraL.setStatusCallback(null);
//                    mUVCCameraL.setButtonCallback(null);
//                    mUVCCameraL.close();
//                    mUVCCameraL.destroy();
//                } catch (final Exception e) {
//                    //
//                }
//                mUVCCameraL = null;
//                leftFrame = false;
//                leftSuc = -1;
//            }
//            if (mLeftPreviewSurface != null) {
//                mLeftPreviewSurface.release();
//                mLeftPreviewSurface = null;
//            }
//        }

//        synchronized (mSync) {
//            if (mUVCCameraR != null) {
//                try {
//                    mUVCCameraR.setStatusCallback(null);
//                    mUVCCameraR.setButtonCallback(null);
//                    mUVCCameraR.close();
//                    mUVCCameraR.destroy();
//                } catch (final Exception e) {
//                    //
//                }
//                mUVCCameraR = null;
//                rightFrame = false;
//                rightSuc = -1;
//            }
//            if (mRightPreviewSurface != null) {
//                mRightPreviewSurface.release();
//                mRightPreviewSurface = null;
//            }
//        }
    }

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            Log.v(TAG, "onAttach:");
            mUSBMonitor.requestPermission(device);

            mHandler.sendEmptyMessage(USB_CONNECT);
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            Log.v(TAG, "onConnect:");
            // openCameraDevice(device, ctrlBlock);
            CameraResStruct crs = new CameraResStruct();
            crs.setCtrlBlock(ctrlBlock);
            usbMap.put(device, crs);
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            Log.v(TAG, "onDisconnect:");

            mHandler.sendEmptyMessage(USB_DISCONNECT);
        }

        @Override
        public void onDettach(final UsbDevice device) {
            Log.v(TAG, "onDettach:");
        }

        @Override
        public void onCancel(final UsbDevice device) {
            Log.v(TAG, "onCancel:");
        }
    };

    private class USBReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            setText("action : " + action);
            Log.d(TAG, "action : " + action);

            if (intent.hasExtra(UsbManager.EXTRA_PERMISSION_GRANTED)) {
                boolean permissionGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                addText("permissionGranted : " + permissionGranted);
            }
            switch (action) {
                case UsbManager.ACTION_USB_ACCESSORY_ATTACHED:
                case UsbManager.ACTION_USB_ACCESSORY_DETACHED:
                    //Name of extra for ACTION_USB_ACCESSORY_ATTACHED and ACTION_USB_ACCESSORY_DETACHED broadcasts containing the UsbAccessory object for the accessory.
                    UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    addText(accessory.toString());
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    //Name of extra for ACTION_USB_DEVICE_ATTACHED and ACTION_USB_DEVICE_DETACHED broadcasts containing the UsbDevice object for the device.
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    addText(device.toString());
                    break;
                case MyUsbManager.ACTION_USB_STATE:
                    /*
                     * <li> {@link #USB_CONNECTED} boolean indicating whether USB is connected or disconnected.
                     * <li> {@link #USB_CONFIGURED} boolean indicating whether USB is configured.
                     * currently zero if not configured, one for configured.
                     * <li> {@link #USB_FUNCTION_ADB} boolean extra indicating whether the
                     * adb function is enabled
                     * <li> {@link #USB_FUNCTION_RNDIS} boolean extra indicating whether the
                     * RNDIS ethernet function is enabled
                     * <li> {@link #USB_FUNCTION_MTP} boolean extra indicating whether the
                     * MTP function is enabled
                     * <li> {@link #USB_FUNCTION_PTP} boolean extra indicating whether the
                     * PTP function is enabled
                     * <li> {@link #USB_FUNCTION_PTP} boolean extra indicating whether the
                     * accessory function is enabled
                     * <li> {@link #USB_FUNCTION_AUDIO_SOURCE} boolean extra indicating whether the
                     * audio source function is enabled
                     * <li> {@link #USB_FUNCTION_MIDI} boolean extra indicating whether the
                     * MIDI function is enabled
                     * </ul>
                     */
                    boolean connected = intent.getBooleanExtra(MyUsbManager.USB_CONNECTED, false);
                    addText("connected : " + connected);
                    boolean configured = intent.getBooleanExtra(MyUsbManager.USB_CONFIGURED, false);
                    addText("configured : " + configured);
                    boolean function_adb = intent.getBooleanExtra(MyUsbManager.USB_FUNCTION_ADB, false);
                    addText("function_adb : " + function_adb);
                    boolean function_rndis = intent.getBooleanExtra(MyUsbManager.USB_FUNCTION_RNDIS, false);
                    addText("function_rndis : " + function_rndis);
                    boolean function_mtp = intent.getBooleanExtra(MyUsbManager.USB_FUNCTION_MTP, false);
                    addText("function_mtp : " + function_mtp);
                    boolean function_ptp = intent.getBooleanExtra(MyUsbManager.USB_FUNCTION_PTP, false);
                    addText("usb_function_ptp : " + function_ptp);
                    boolean function_audio_source = intent.getBooleanExtra(MyUsbManager.USB_FUNCTION_AUDIO_SOURCE, false);
                    addText("function_audio_source : " + function_audio_source);
                    boolean function_midi = intent.getBooleanExtra(MyUsbManager.USB_FUNCTION_MIDI, false);
                    addText("function_midi : " + function_midi);
                    break;
            }
        }
    }

    private class USBBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.d(TAG, "into onReceive==>>" + intent.getAction());
            if (intent.getAction().equals("android.hardware.usb.action.USB_STATE")) {
                Log.d(TAG, "into onReceive==>>" + intent.getExtras());

                if (intent.getExtras().getBoolean("connected")) {
                    // usb 插入
                    Toast.makeText(MainActivity.this, "usb 插入", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "usb 插入");
                } else {
                    //   usb 拔出
                    Toast.makeText(MainActivity.this, "usb 拔出", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "usb 拔出");
                }
                onUpdateUSBDevices(null);
            }
        }
    }

    private void addText(String str) {
        tv_usb_info.setText(tv_usb_info.getText().toString() + str + "\n");
    }

    private void setText(String str) {
        tv_usb_info.setText(str + "\n");
    }

    private class RecordAmplitude extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (isRecording) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AudioManager audioManager = null;
               // audioManager.setMode(AudioManager.ROUTE_SPEAKER);
                int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

                int value = recorder.getMaxAmplitude();
                Log.d(TAG, "==getMaxAmplitude=>>" + value + "=new currVolume=>>" + currVolume);
                publishProgress(value);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "==>>" + values[0]);
            audioProgressBar.setProgress(values[0] % 100);
        }
    }


    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "收到本地广播", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "收到本地广播");
            if (intent.getAction().equals(ACTION_INTENT_RECEIVER)) {
                UVCStruct uvcStruct = (UVCStruct) intent.getSerializableExtra("UVCStruct");
                UVCCameraTextureView uvcCameraTextureView = uvcStruct.getmUVCCameraView();
                Iterator iter = usbMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    UVCCameraTextureView uvcView = ((CameraResStruct) entry.getValue()).getmUVCCameraView();
                    if (uvcCameraTextureView == uvcView) {
                        USBMonitor.UsbControlBlock ctrlBlock = ((CameraResStruct) entry.getValue()).getCtrlBlock();
                        openCameraDevice(ctrlBlock, uvcView);
                    }
                }
            }
        }
    }

    // 处理权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权成功，继续打电话
                    //callPhone(tel);
                } else {
                    // 授权失败！
                    Toast.makeText(this, "授权失败！", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }
}
