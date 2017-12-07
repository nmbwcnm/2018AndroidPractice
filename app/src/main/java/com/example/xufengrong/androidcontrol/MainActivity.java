package com.example.xufengrong.androidcontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by xufengrong on 2017/12/6.
 */

public class MainActivity extends Activity implements View.OnClickListener{

        //定义扫描设备按钮
        Button scanButton;

        //定义蓝牙适配器
        BluetoothAdapter mBluetoothAdapter;

        //定义蓝牙设备管理器
        BluetoothManager bluetoothManager;

        public int REQUEST_ENABLE_BT = 1;
        @Override
        public void onCreate(Bundle savedInstanceState){
                Log.d("MainActivity","onCreate");
                super.onCreate(savedInstanceState);

                setContentView(R.layout.main_activity_layout);
                UiInit();
                BLeInit();
        }

        @Override
        public void onClick(View view) {


        }


        //UI界面初始化
        private void UiInit(){
                Log.d("MainActivity","UiInit");
                scanButton = findViewById(R.id.ScanButton);
                scanButton.setOnClickListener(this);//对此按钮进行监听
        }

        private void BLeInit(){
                Log.d("MainActivity","BLeInit");
                final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
                if(mBluetoothAdapter==null||!mBluetoothAdapter.isEnabled()){
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,1);
                }
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

//        public void



}
