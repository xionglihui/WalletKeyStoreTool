package com.xiong.wallet.keystoretool.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.xiong.wallet.keystoretool.R;
import com.xiong.wallet.keystoretool.entity.WalletKeyPair;
import com.xiong.wallet.keystoretool.utils.GJsonUtil;
import com.xiong.wallet.keystoretool.utils.WalletUtils;


/**
 * Created by xionglh on 2018/8/20
 */
public class MainActivity extends Activity implements View.OnClickListener {


    private EditText mEdtKeys, mEdtPwd, mEdtKeyStore, mEdtStore, mEdtPrivate;

    private String mPrivateKey = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        new CreateKeysAsyncTask().execute();
    }

    private void initViews() {
        mEdtKeys = (EditText) findViewById(R.id.edt_main_key);
        mEdtStore = (EditText) findViewById(R.id.edt_main_keySotre);
        mEdtPwd = (EditText) findViewById(R.id.edt_main_pwd);
        mEdtKeyStore = (EditText) findViewById(R.id.edt_main_keystore);
        mEdtPrivate = (EditText) findViewById(R.id.edt_main_private);
        findViewById(R.id.btn_pwd).setOnClickListener(this);
        findViewById(R.id.btn_keystore).setOnClickListener(this);
    }

    private void crateKeyStore() {
        if ("".equals(mPrivateKey))
            return;
        String pwd = mEdtPwd.getText().toString();
        String keySore = WalletUtils.createKeyStore(pwd, mPrivateKey);
        mEdtStore.setText(keySore);
    }

    private void recoverKeyStore() {
        String pwd = mEdtPwd.getText().toString();
        String keystore = mEdtKeyStore.getText().toString();
        String keys = WalletUtils.recoverKeyStore(pwd, keystore);
        mEdtPrivate.setText(keys);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pwd:

                crateKeyStore();
                break;
            case R.id.btn_keystore:
                recoverKeyStore();
                break;
        }
    }


    private class CreateKeysAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return WalletUtils.createWalletKeyPairStr();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("keys", s);
            WalletKeyPair walletKeyPair = GJsonUtil.toObject(s, WalletKeyPair.class);
            mPrivateKey = walletKeyPair.getPrivateKey();
            mEdtKeys.setText(s);
            super.onPostExecute(s);
        }
    }

}
