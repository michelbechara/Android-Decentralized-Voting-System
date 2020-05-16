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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mbproductions.dynamicvotingsystemandroid.Contract.VotingSystem;
import com.mbproductions.dynamicvotingsystemandroid.R;
import com.mbproductions.dynamicvotingsystemandroid.RecyclerView.Contracts_Adapter;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

public class NewCandidateActivity extends AppCompatActivity {

    Button add_btn;
    TextInputLayout CName_input;
    Spinner gender_spinner;


    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(110000);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(3000000000L);

    ArrayAdapter<CharSequence> gender_adapter;

    String newcandidatename = "";
    boolean isMale = false;

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_candidate);

        init();
        Add_btn();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void init() {
        add_btn = findViewById(R.id.add_button_candidate);
        CName_input = findViewById(R.id.CandidateName_input);
        gender_spinner = findViewById(R.id.gender_spinner);

        /////////////////// Manage Spinner //////////////////////////////////////////////

        gender_adapter = ArrayAdapter.createFromResource(this, R.array.Gender, android.R.layout.simple_spinner_item);
        gender_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(gender_adapter);

        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    add_btn.setVisibility(View.GONE);
                } else {
                    if (position == 1) {
                        isMale = true;
                        add_btn.setVisibility(View.VISIBLE);
                    } else if (position == 2) {
                        isMale = false;
                        add_btn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                add_btn.setVisibility(View.GONE);
            }
        });



        //////////////////////////////////////////////////////////////////////

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void Add_btn() {
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = String.valueOf(CName_input.getEditText().getText());
                if (a != "") {


                    handler = new Handler();
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                        }
                    };

                    handler.postDelayed(runnable, 3000);

                    newcandidatename = a;

                    ////////////////////////////Get Spinner Value////////////////////////////////////////////////



                    ///////////////////////////////////////////////////////////////////////////////////////////////

                    NewCandidateActivity.AddCandidateAsync ac = new NewCandidateActivity.AddCandidateAsync();
                    ac.execute("");

                } else {
                    Toast.makeText(NewCandidateActivity.this, "Please enter a Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public class AddCandidateAsync extends AsyncTask {

        @Override
        protected Void doInBackground(Object... objects) {
            Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/0642d3f26469482c984b589d336fcc80"));

            String PRIVATE_KEY = "BFE15071308C18FFF3AA08E27091530A6F8757F9D07A1A273592B2041F1E1412";
            Credentials credentials = null;

            credentials = Credentials.create(PRIVATE_KEY);//WalletUtils.loadCredentials("tacobell5", "resources/wallet/UTC--2019-04-24T06-29-12.770000000Z--d475f295562d0052ccc2b5cf0576e03c1d1e852a.json");
            String CONTRACT_ADDRESS = Contracts_Adapter.Election_Contract_Address;

            Log.d("addddddd", CONTRACT_ADDRESS);

            //ContractGasProvider contractGasProvider = new DefaultGasProvider();

            VotingSystem votingSystem = LoadContract(CONTRACT_ADDRESS, web3j, credentials);

            try {

                ContractGasProvider contractGasProvider = new ContractGasProvider() {
                    @Override
                    public BigInteger getGasPrice(String contractFunc) {
                        return GAS_PRICE;
                    }

                    @Override
                    public BigInteger getGasPrice() {
                        return GAS_PRICE;
                    }

                    @Override
                    public BigInteger getGasLimit(String contractFunc) {
                        return GAS_LIMIT;
                    }

                    @Override
                    public BigInteger getGasLimit() {
                        return GAS_LIMIT;
                    }
                };

                //contractGasProvider = new DefaultGasProvider();

                Log.d("priceeeeeee", String.valueOf(votingSystem.getTransactionReceipt()));

                votingSystem.setGasProvider(contractGasProvider);

                runOnUiThread(new Runnable() {


                    @Override
                    public void run() {
                        setProgressDialog(true);

                    }
                });

                votingSystem.addCandidate(newcandidatename, isMale).send();

                setProgressDialog(false);




            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    finish();
                    Intent intent = new Intent(NewCandidateActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            });

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            Log.d("done: ", "donez sending candidate");

            return null;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    ContractGasProvider contractGasProvider = new DefaultGasProvider();

    private VotingSystem LoadContract(String contractAddress, Web3j web3j, Credentials credentials) {
        return VotingSystem.load(contractAddress, web3j, credentials, contractGasProvider);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private String deployContract(Web3j web3j, Credentials credentials) throws Exception {
        ContractGasProvider contractGasProvider = new DefaultGasProvider();
        return VotingSystem.deploy(web3j, credentials, contractGasProvider)
                .send()
                .getContractAddress();
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
            tvText.setText("Adding ...");
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
