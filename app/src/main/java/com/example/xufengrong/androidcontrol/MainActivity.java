package com.example.xufengrong.androidcontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.Inflater;

/**
 * Created by xufengrong on 2017/12/6.
 */

public class MainActivity extends Activity implements View.OnClickListener{

        boolean scanFlag=true;
        //定义扫描设备按钮
        Button scanButton;

        //定义蓝牙适配器
        BluetoothAdapter mBluetoothAdapter;

        //定义蓝牙扫描器，由于5.1版本似乎不再支持android - mBluetoothAdapter.stopLeScan(mLeScanCallback);
        // 解决办法http://www.itstrike.cn/Question/0991ce49-5ee3-4d5c-ab2b-861502baa799.html

        //定义蓝牙扫描器
        BluetoothLeScanner scanner;

        //定义蓝牙设备管理器
        BluetoothManager bluetoothManager;

        //定义扫描状态开关
        boolean mScanFlag=false;

        //定义扫描状态
        boolean mScanning=false;

        //自定义的一个扫描出来来显示的设备适配器
        private  LeDeviceListAdapter mLeDeviceListAdapter;

        //list 显示
        ListView listView;

        private ArrayList<Integer> rssis;

        //定义一个扫描周期
        private static final long SCAN_PERIOD = 10000;

        public int REQUEST_ENABLE_BT = 1;

        private Handler mHandler;
        @Override
        public void onCreate(Bundle savedInstanceState){
                Log.d("MainActivity","onCreate");
                super.onCreate(savedInstanceState);

                setContentView(R.layout.main_activity_layout);
                //这些初始化要在视图加载的后面不然会闪退
                UiInit();
                BLeInit();

                mLeDeviceListAdapter=new LeDeviceListAdapter();
                listView.setAdapter(mLeDeviceListAdapter);
        }

        @Override
        public void onClick(View view) {
                if(scanFlag==true){
                        scanBLEDevice(true);
                        mLeDeviceListAdapter = new LeDeviceListAdapter();
                        listView.setAdapter(mLeDeviceListAdapter);
                }else if(scanFlag==false){
                        scanBLEDevice(false);
                }


                //改变按钮的文字
                if(mScanning==true){
                        scanButton.setText("停止扫描蓝牙BLE设备");
                }
                else
                {
                        scanButton.setText("开始扫描蓝牙BLE设备");
                }
        }


        //
        public void scanBLEDevice(final boolean enable){
                Log.d("Mian","1\r\n");
                if(enable) {
                        Log.d("Mian","2\r\n");
                        mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                        mScanning = false;
                                        scanner.stopScan(mLeScanCallback);
                                        Log.d("MainActivity","stopScanning");
                                }
                        }, SCAN_PERIOD);
                       mScanning = true;
                       scanner.startScan(mLeScanCallback);
                }else {
                        mScanning=false;
                        scanner.stopScan(mLeScanCallback);
                        Log.d("MainActivity","3\r\n");
                }

        }

        private ScanCallback mLeScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                        super.onScanResult(callbackType, result);
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        mLeDeviceListAdapter.addDevice(result.getDevice(),result.getRssi());
                                        mLeDeviceListAdapter.notifyDataSetChanged();
                                }
                        });
                }
                @Override
                public void onScanFailed(int errorCode){
                    super.onScanFailed(errorCode);
                    Log.v("MainActivity", String.valueOf(errorCode));
                }
        };


        //UI界面初始化
        private void UiInit(){
                Log.d("MainActivity","UiInit");
                scanButton = findViewById(R.id.ScanButton);
                scanButton.setOnClickListener(this);//对此按钮进行监听
                mHandler=new Handler();
                listView=findViewById(R.id.deviceList);
        }

        private void BLeInit(){

                Log.d("MainActivity","BLeInit");
                final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
                if(mBluetoothAdapter==null||!mBluetoothAdapter.isEnabled()){
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,1);
                }
                scanner=mBluetoothAdapter.getBluetoothLeScanner();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data){
                Log.d("MainActivity","onActivityResult");
                switch (requestCode){
                        case 1:
                                if(resultCode==RESULT_CANCELED){
                                        Log.d("MainActivity","onActivityResultRESULT_CANCELED");
                                        Toast.makeText(this,"蓝牙不开你想上天？",Toast.LENGTH_SHORT).show();
                                        finish();
                                }
                         break;
                }
        }

        @Override
        public void onResume(){
                super.onResume();
        }
        @Override
        public void onDestroy(){
                Log.d("MainActivity","onDestroy");
                super.onDestroy();
        }

        public void onRestart(){
                Log.d("MainActivity","onDestroy");
                super.onRestart();
        }


        private  class LeDeviceListAdapter extends BaseAdapter{

                ArrayList<BluetoothDevice> mBluetoothDevice;

                private LayoutInflater mInflater;

                public LeDeviceListAdapter(){
                        super();
                        rssis=new ArrayList<Integer>();
                        mBluetoothDevice =new ArrayList<BluetoothDevice>();
                        mInflater = getLayoutInflater();
                }

                public void addDevice(BluetoothDevice device,int rssi){
                        if(!mBluetoothDevice.contains(device)){
                                Log.d("MainActivity","addDevice");
                                mBluetoothDevice.add(device);
                                rssis.add(rssi);
                        }
                }



                @Override
                public int getCount() {
                        return mBluetoothDevice.size();
                }

                @Override
                public Object getItem(int position) {
                        return mBluetoothDevice.get(position);
                }

                @Override
                public long getItemId(int position) {
                        return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                        View view;
                        Log.d("MainActivity","getView");
                        if(convertView==null)
                        {
                                Log.d("MainActivity","convertViewNull");
                                view= mInflater.inflate(R.layout.device_layout,null);
                        }else
                        {
                                Log.d("MainActivity","convertViewNotNull");
                                view = convertView;
                        }

//在这里遇到了Bug首先在下面的实例中没有加view. 。。。导致编译器在Main_activity_layout中找name address等Id找不到报错
//                        认为这这些实例是空的需要重新敲下面这三个语句
                        TextView nameView=(TextView)view.findViewById(R.id.Name);
                        TextView addressView=(TextView)view.findViewById(R.id.Address);
                        TextView rssisView=(TextView)view.findViewById(R.id.DeviceRssi);
                        if(view==null)
                        {
                                Log.d("MainActivity","Null Object");
                        }
                        //position 是在list列表中的位置 在这里它拿到了当前这个设备在list的position
                        BluetoothDevice bluetoothDevice = mBluetoothDevice.get(position);
//
                        nameView.setText(bluetoothDevice.getName());
                        addressView.setText(bluetoothDevice.getAddress());
                        //setText中不能传递Int型数据只能传递字符串型通过下面的方法可以转换成string型
                        rssisView.setText(""+rssis.get(position));

                        return view;
                }
        }





}
