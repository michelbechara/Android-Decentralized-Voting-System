package com.mbproductions.dynamicvotingsystemandroid.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mbproductions.dynamicvotingsystemandroid.Contract.Contracts_DB;
import com.mbproductions.dynamicvotingsystemandroid.Contract.VotingSystem;
import com.mbproductions.dynamicvotingsystemandroid.R;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

public class NewContractActivity extends AppCompatActivity {

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(2100000L);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(2000000000L);

    Button add_btn;
    TextInputLayout EName_input;
    String newcontractname="";
    String deployedAddress="";

    Handler handler;
    Runnable runnable;

    static ProgressBar progressBar;
    static TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contract);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //to make a back button on top, use this and add a parent in manifest

        init();

        Add_btn();

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void init()
    {
        add_btn = findViewById(R.id.add_button);
        EName_input = findViewById(R.id.ElectionName_input);

    }

    ContractsActivity contractsActivity;

    private void Add_btn()
    {
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = String.valueOf(EName_input.getEditText().getText());
                if(a!="") {

                    newcontractname = a;
                    NewContractActivity.AddContractAsync dc = new NewContractActivity.AddContractAsync();
                    dc.execute("");

                }
                else{
                    Toast.makeText(NewContractActivity.this, "Please enter a Name", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public class AddContractAsync extends AsyncTask {

        @Override
        protected Void doInBackground(Object... objects) {
            Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/0642d3f26469482c984b589d336fcc80"));

            String PRIVATE_KEY = "BFE15071308C18FFF3AA08E27091530A6F8757F9D07A1A273592B2041F1E1412";
            Credentials credentials = null;
            //String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            credentials = Credentials.create(PRIVATE_KEY);//WalletUtils.loadCredentials("tacobell5", "resources/wallet/UTC--2019-04-24T06-29-12.770000000Z--d475f295562d0052ccc2b5cf0576e03c1d1e852a.json");


            ContractGasProvider contractGasProvider = new DefaultGasProvider();
            try {

                runOnUiThread(new Runnable() {


                    @Override
                    public void run() {
                        setProgressDialog(true);

                    }
                });

                deployedAddress = deployContract(web3j, credentials);
                Log.d("deployed address: ", deployedAddress);
                Log.d("nameee: ", newcontractname);

                Contracts_DB contracts_db = LoadContract(ContractsActivity.CONTRACT_ADDRESS, web3j, credentials);
                Log.d("loadedd: ", "loadeddd");

                contracts_db.setGasProvider(contractGasProvider);
                contracts_db.addContract(newcontractname,deployedAddress).send();
                setProgressDialog(false);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        //contractsActivity.finish();
                        finish();
                        Intent intent = new Intent(NewContractActivity.this,ContractsActivity.class);
                        startActivity(intent);

                    }
                });

                Log.d("done: ", "donez");


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////

        ContractGasProvider contractGasProvider = new DefaultGasProvider();

        private Contracts_DB LoadContract(String contractAddress, Web3j web3j, Credentials credentials) {
            return Contracts_DB.load(contractAddress, web3j, credentials, contractGasProvider);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////

        private String deployContract(Web3j web3j, Credentials credentials) throws Exception {
            ContractGasProvider contractGasProvider = new DefaultGasProvider();
            return VotingSystem.deploy(web3j,credentials,contractGasProvider)
                    .send()
                    .getContractAddress();
        }
    }

    AlertDialog dialog;

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

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
