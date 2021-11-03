package com.qihoo.ailabtest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.widget.Toast;

import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.qihoo.ailab.AIRuleUIReactive;
import com.qihoo.ailab.model.AIRule;
import com.qihoo.ailab.util.L;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AIRuleUIReactive ruleRct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPm();
        ruleRct = new AIRuleUIReactive(this, AIRule.test());
        ruleRct.load();
        ruleRct.onClickCheck("", "report_warning", false);
        ruleRct.onBlockSelectedTitle("","sensitive_select", "低");
        ruleRct.onRegionData("","region_set","[{\"x\":0.11231,\"y\":0.1},{\"x\":0.1232131,\"y\":0.1},{\"x\":0.1123123,\"y\":0.1}],[{\"x\":0.1,\"y\":0.1},{\"x\":0.1,\"y\":0.1},{\"x\":0.1,\"y\":0.1}]");
        log(ruleRct.getRuleSettingsJson());

        log(ruleRct.getWorkBlockTypes());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ruleRct.getLuaFilePath(new CodeGenerationRequest.CodeGeneratorCallback() {
                    @Override
                    public void onFinishCodeGeneration(String generatedCode) {
                        log(generatedCode);
                    }
                });
            }
        }, 3000);
        log(ruleRct.saveWorkspaceXml());
        new Thread(){
            @Override
            public void run() {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(ruleRct.saveWorkspaceXml()));
                    String line = null;
                    while((line = reader.readLine())!=null){
                        log(line);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if(reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    private void checkPm() {
        int permission_write= ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_read=ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission_write!= PackageManager.PERMISSION_GRANTED
                || permission_read!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "正在请求权限", Toast.LENGTH_SHORT).show();
            //申请权限，特征码自定义为1，可在回调时进行相关判断
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //权限已成功申请
                }else{
                    //用户拒绝授权
                    Toast.makeText(this, "无法获取SD卡读写权限", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void log(String msg){
        L.d(TAG, ""+msg);
    }
}