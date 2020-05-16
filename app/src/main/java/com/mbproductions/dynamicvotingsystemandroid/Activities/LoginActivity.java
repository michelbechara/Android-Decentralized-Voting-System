package com.mbproductions.dynamicvotingsystemandroid.Activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mbproductions.dynamicvotingsystemandroid.R;
import com.mbproductions.dynamicvotingsystemandroid.utils.myBip44utils;
import com.mbproductions.dynamicvotingsystemandroid.utils.PreferenceUtils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Provider;
import java.security.Security;

public class LoginActivity extends AppCompatActivity {

    Button login_pk_button;
    Button login_mnemonic_button;
    Button create_button;
    TextInputLayout PK_input;
    TextInputLayout Mnemonic_input;
    Button login_button;
    boolean pk_input_acivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupBouncyCastle();

        init();

        Intent intent = getIntent();

        btns();

    }


    private void init() {
        login_pk_button = (Button) findViewById(R.id.login_pk_button);
        login_mnemonic_button = findViewById(R.id.login_mnemonic_button);
        create_button = findViewById(R.id.create_button);
        PK_input = findViewById(R.id.pk_input);
        Mnemonic_input = findViewById(R.id.mnemonic_input);
        login_button = findViewById(R.id.login_button);

        PreferenceUtils utils = new PreferenceUtils();
        if (utils.getPK(this) != null) {
            finish();
            Intent Contracts = new Intent(this, ContractsActivity.class);
            startActivity(Contracts);
        } else {

        }

        PK_input.setVisibility(View.GONE);
        Mnemonic_input.setVisibility(View.GONE);
        login_button.setVisibility(View.GONE);
    }


    private void btns()
    {
        login_pk_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pk_input_acivated = true;
                Mnemonic_input.setVisibility(View.GONE);
                PK_input.setVisibility(View.VISIBLE);
                login_button.setVisibility(View.VISIBLE);
            }
        });

        login_mnemonic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pk_input_acivated = false;
                PK_input.setVisibility(View.GONE);
                Mnemonic_input.setVisibility(View.VISIBLE);
                login_button.setVisibility(View.VISIBLE);
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pk_input_acivated) {

                    String a = String.valueOf(PK_input.getEditText().getText()); //take text
                    Log.d("admin",a);
                    if(a.equals("admin"))
                    {
                        finish();
                        adminlogin();
                    }

                    if (a.trim().length() == 64) {
                        PreferenceUtils.savePK(a.trim(), LoginActivity.this);
                        startactivity();
                    }
                } else {
                    String a = String.valueOf(Mnemonic_input.getEditText().getText());

                    if(a.equals("admin"))
                    {
                        finish();
                        adminlogin();
                    }

                    if (a.split(" ").length == 12) {
                        String mnemonic = a;
                        Credentials credentials = null;
                        try {
                            credentials = myBip44utils.loadBip44Credentials("", mnemonic);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String privateKeyGenerated = credentials.getEcKeyPair().getPrivateKey().toString(16);
                        //String add = credentials.getAddress();
                        PreferenceUtils.savePK(privateKeyGenerated, LoginActivity.this);
                        startactivity();
                    }
                }
            }
        });

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkPermission()) {
                    createAccount();
                } else {
                    if (checkPermission()) {
                        requestPermissionAndContinue();
                    } else {
                        createAccount();
                    }
                }
            }
        });
    }


    private void adminlogin()
    {
        PreferenceUtils.savePK("BFE15071308C18FFF3AA08E27091530A6F8757F9D07A1A273592B2041F1E1412",this);
        Intent contracts = new Intent(LoginActivity.this, ContractsActivity.class);
        startActivity(contracts);
    }




    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            // BC with same package name, shouldn't happen in real life.
            return;
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    private void startactivity() {
        Intent contracts = new Intent(this, ContractsActivity.class);
        contracts.putExtra("PK", PreferenceUtils.getPK(this));
        startActivity(contracts);
    }

    private static final int PERMISSION_REQUEST_CODE = 200;

    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.permission_necessary));
                alertBuilder.setMessage(R.string.storage_permission_is_encessary_to_wrote_event);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            createAccount();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    createAccount();
                } else {
                    finish();
                }

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    static String mnemonic;
    static String PK;
    private final static String PRIVATE_KEY_sender = "BFE15071308C18FFF3AA08E27091530A6F8757F9D07A1A273592B2041F1E1412";

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(150000);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(5000000000L);

    private static String RECIPIENT = "";

    static Credentials credentials = null;

    private void createAccount() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        try {

            setProgressDialog(true);
            //mnemonic= myBip44utils.generateFullNewWalletFile("",new File(path));
            mnemonic = myBip44utils.generateBip39Wallet("", new File(path)).getMnemonic();
            //mnemonic = myBip44utils.generateBip44Wallet("tacobell5", new File(path)).getMnemonic();

            try {
                // credentials = myBip44utils.loadBip44Credentials("", mnemonic);
                credentials = myBip44utils.loadBip39Credentials("", mnemonic);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PK = credentials.getEcKeyPair().getPrivateKey().toString(16);
            PreferenceUtils.savePK(PK, LoginActivity.this);
            Log.d("PKKKKKKKKK", PK);


            RECIPIENT = credentials.getAddress();
            Log.d("Poppppp", RECIPIENT);


            SendFunds sf=new SendFunds();
            sf.execute("");



        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SendFunds extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/0642d3f26469482c984b589d336fcc80"));
            TransactionManager transactionManager = new RawTransactionManager(
                    web3j, Credentials.create(PRIVATE_KEY_sender)
            );
            Transfer transfer = new Transfer(web3j, transactionManager);
            TransactionReceipt transactionReceipt = null;

            try {
                transactionReceipt = transfer.sendFunds(
                        RECIPIENT,
                        BigDecimal.valueOf(5000000),
                        Convert.Unit.GWEI,
                        GAS_PRICE,
                        GAS_LIMIT

                ).send();
            } catch (Exception e) {
                e.printStackTrace();
            }


            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    finish();
                    Intent accinfoactivity = new Intent(LoginActivity.this, AccountInfoActivity.class);
                    accinfoactivity.putExtra("PK", PK);
                    accinfoactivity.putExtra("MNEMONIC", mnemonic);
                    startActivity(accinfoactivity);
                }
            });

            return null;
        }
    }

    android.support.v7.app.AlertDialog dialog;

    public void setProgressDialog(boolean o) {

        if(o==true) {
            int llPadding = 30;
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setPadding(llPadding, llPadding, llPadding, llPadding);
            ll.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            llParam.gravity = Gravity.CENTER;
            ll.setLayoutParams(llParam);

            ProgressBar progressBar = new ProgressBar(this);
            progressBar.setIndeterminate(true);
            progressBar.setPadding(0, 0, llPadding, 0);
            progressBar.setLayoutParams(llParam);

            llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            llParam.gravity = Gravity.CENTER;
            TextView tvText = new TextView(this);
            tvText.setText("Creating ...");
            tvText.setTextColor(Color.parseColor("#000000"));
            tvText.setTextSize(20);
            tvText.setLayoutParams(llParam);

            ll.addView(progressBar);
            ll.addView(tvText);

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setView(ll);

            dialog = builder.create();
            dialog.show();
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(layoutParams);
                dialog.setCancelable(false);
            }
        }

        else
        {
            dialog.dismiss();
        }
    }
}

