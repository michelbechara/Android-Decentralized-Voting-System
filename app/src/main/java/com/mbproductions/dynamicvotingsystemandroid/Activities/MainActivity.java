package com.mbproductions.dynamicvotingsystemandroid.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import java.io.File;
import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import com.mbproductions.dynamicvotingsystemandroid.Contract.VotingSystem;
import com.mbproductions.dynamicvotingsystemandroid.R;
import com.mbproductions.dynamicvotingsystemandroid.RecyclerView.Adapter;
import com.mbproductions.dynamicvotingsystemandroid.RecyclerView.Contracts_Adapter;
import com.mbproductions.dynamicvotingsystemandroid.utils.PreferenceUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ProgressBar progressBar;
    static TextView loadingText;
    static TextView processingText;
    static TextView thankyouText;
    static TextView failedText;
    static Button castVote_btn;

    FloatingActionButton fab;

    Adapter Candidates_adapter = null;
    private Paint p = new Paint();

    Handler handler;
    Runnable runnable;
    static String PK = "";
    static boolean isadmin = false;

    //private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(210000L);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(2000000000L);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setupBouncyCastle();
        init();


        ////////////////////////////////////////////////////////////////////

        runTask1(); //check whether internet connection is available and run async task from there before loading data

        ////////////////////////////////////////////////////////////////////

        castVote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Adapter.candidateIDselected == 0) {
                    Toast.makeText(getBaseContext(), "Please select a candidate", Toast.LENGTH_SHORT).show();
                } else {
                    runTask2(); //check whether internet connection is available and run async task from there before voting
                }
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void init() {
        ////////////////////////////////////////////////////////////////////
        Intent intent = getIntent();
        recyclerView = findViewById(R.id.rv);

        if (intent.hasExtra("PK")) {
            PK = getIntent().getStringExtra("PK");
            Toast.makeText(this, PK, Toast.LENGTH_SHORT).show();
        } else {
            PK = PreferenceUtils.getPK(this);
            Toast.makeText(this, PK, Toast.LENGTH_SHORT).show();
        }

        if (PK.equals("BFE15071308C18FFF3AA08E27091530A6F8757F9D07A1A273592B2041F1E1412")) {
            isadmin = true;
        }

        ////////////////////////////////////////////////////////////////////

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        loadingText = (TextView) findViewById(R.id.loadingtext);
        processingText = (TextView) findViewById(R.id.processingtext);
        thankyouText = (TextView) findViewById(R.id.thankyoutext);
        failedText = findViewById(R.id.failedtext);
        castVote_btn = (Button) findViewById(R.id.castvote_btn);
        castVote_btn.setVisibility(View.GONE);
        processingText.setVisibility(View.GONE);
        failedText.setVisibility(View.GONE);
        fab = findViewById(R.id.fab_main);

        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorFABbgTint)));
        fab.setVisibility(View.GONE);

        thankyouText.setVisibility(View.GONE);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        this.handler.postDelayed(this.runnable, 3000);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(MainActivity.this, NewCandidateActivity.class);
                startActivity(a);
            }
        });
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String createWallet() throws Exception {
        WalletFile wallet = null;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        Log.d("pathhhh", path);
        String fileName = WalletUtils.generateLightNewWalletFile("tacobell5", new File(path));
        Log.d("pathhhh", fileName);
        return path + "/" + fileName;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////


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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void runTask1() {
        if (haveNetworkConnection()) {
            LoadDataAsync lda = new LoadDataAsync();
            lda.execute(""); //to get number of candidates

            if (lda.getStatus() == LoadDataAsync.Status.FINISHED) {
                progressBar.setVisibility(View.GONE);
            }
        } else {
            AlertDialog.Builder msgbox1 = new AlertDialog.Builder(this);
            msgbox1.setTitle("No Internet Connection");
            msgbox1.setMessage("Please connect to WiFi or Mobile Internet in order to proceed");
            msgbox1.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    runTask1();
                }
            });
            msgbox1.setCancelable(false);
            msgbox1.create().show();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void runTask2() {
        if (haveNetworkConnection()) {
            castVote_btn.setVisibility(View.GONE);
            failedText.setVisibility(View.GONE);
            processingText.setVisibility(View.VISIBLE);
            VoteForCandidate vfc = new VoteForCandidate();
            vfc.execute("");

        } else {
            AlertDialog.Builder msgbox1 = new AlertDialog.Builder(this);
            msgbox1.setCancelable(false);
            msgbox1.setTitle("No Internet Connection");
            msgbox1.setMessage("Please connect to WiFi or Mobile Internet in order to proceed");
            msgbox1.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    runTask2();
                }
            });

            msgbox1.setCancelable(false);
            msgbox1.create().show();


        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public class VoteForCandidate extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/0642d3f26469482c984b589d336fcc80"));
            String PRIVATE_KEY = PK;
            Credentials credentials = null;
            credentials = Credentials.create(PRIVATE_KEY);
            Log.d("piffff", credentials.getAddress());
            //String CONTRACT_ADDRESS = "0x4c481bef57a76afdd92546336d653e3d0e79ddb4";
            VotingSystem votingSystem = LoadContract(Contracts_Adapter.Election_Contract_Address, web3j, credentials);

            String receipthash = "";
            boolean succeeded = false;
            BigInteger cID = BigInteger.valueOf(Adapter.candidateIDselected);
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

                votingSystem.setGasProvider(contractGasProvider);
                Log.d("theeeeiddd", String.valueOf(Adapter.candidateIDselected));
                votingSystem.vote(BigInteger.valueOf(Adapter.candidateIDselected)).send();
                //vote for candidate
                // Log.d("kkkkkkkkk", );

                succeeded = true;
            } catch (Exception e) {
                Log.d("not successful", "not successful");
                succeeded = false;
                e.printStackTrace();
            }


            if (succeeded) {
                finish();
                startActivity(getIntent());
            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        processingText.setVisibility(View.GONE);
                        failedText.setVisibility(View.VISIBLE);


                    }
                });

            }
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<Integer> SumVotes = new ArrayList<>();


    public class LoadDataAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/0642d3f26469482c984b589d336fcc80"));
            String PRIVATE_KEY = PK;
            Credentials credentials = null;
            credentials = Credentials.create(PRIVATE_KEY);
            //String CONTRACT_ADDRESS = "0x4c481bef57a76afdd92546336d653e3d0e79ddb4";
            VotingSystem votingSystem = LoadContract(Contracts_Adapter.Election_Contract_Address, web3j, credentials);
            Log.d("deployed address: ", votingSystem.getContractAddress());
            BigInteger candidatesnumber = null;
            try {
                candidatesnumber = votingSystem.candidatesNumber().send();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //candidates number is a Bigint (candidates takes big int as param)
            //a is an int (cannot apply <= to a Big int)
            int a = candidatesnumber.intValue();
            Log.d("aaaaaaa", String.valueOf(a));
            //int i=1;


            boolean gender = false;
            int[] images = new int[a];

            int counter=0;

            for (int i = 1; i <= a; i++) {
                Log.d("loopppp", "Looooppp");
                RemoteCall<Tuple4<BigInteger, String, BigInteger, Boolean>> candidate = null;
                try {
                    candidate = votingSystem.candidates(BigInteger.valueOf(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Log.d("cccccc", String.valueOf(candidate.send()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int id = 0, votecount = 0;

                String name = "";
                try {
                    id = candidate.send().getValue1().intValue(); //get value of id from tuple
                    name = candidate.send().getValue2(); //get value of name from tuple
                    votecount = candidate.send().getValue3().intValue(); //get value of votecount from tuple

                    gender = candidate.send().getValue4().booleanValue();
                        Log.d("name", name);
                        Log.d("id", String.valueOf(id));
                        Log.d("votecount", String.valueOf(votecount));

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if(id!=0) {
                    ids.add(id);
                    Log.d("idsssss", String.valueOf(ids));
                    names.add(name);
                    SumVotes.add(votecount);


                    Log.d("genderrrrr", String.valueOf(gender));

                    if (gender) {
                        Log.d("hereeeeee", "hello222");
                        images[counter] = R.drawable.avatarjad;
                        counter++;
                    } else {
                        Log.d("hereeeeee", "hello");
                        images[counter] = R.drawable.avatarmira;
                        counter++;
                    }

                }

                Log.d("picccc", String.valueOf(images));
            }

            final String addr = credentials.getAddress();
            Log.d("feer", addr);
            boolean voter = false;
            int i = 0;
            try {
                voter = votingSystem.voters(addr).send();
            } catch (Exception e) {
                e.printStackTrace();
            }

            final boolean isavoter = voter;

            boolean finalGender = gender;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    RecyclerView recyclerView = findViewById(R.id.rv);
                    Candidates_adapter = new Adapter(getApplicationContext(), ids, names, SumVotes, finalGender, images);
                    recyclerView.setAdapter(Candidates_adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    if (isavoter == true) {
                        castVote_btn.setVisibility(View.GONE);
                        thankyouText.setVisibility(View.VISIBLE);
                    } else {
                        castVote_btn.setVisibility(View.VISIBLE);
                    }

                    if(isadmin)
                    {
                        enableSwipe();
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            });


            return null;
        }
    }

    //String CONTRACT_ADDRESS = "0x2f60fd383d0908effc2c422e7d81aa952db14b46";


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Credentials getCredentialsFromWallet() throws IOException, CipherException {
        return WalletUtils.loadCredentials(
                "tacobell5",
                "resources/wallet/UTC--2019-04-24T06-29-12.770000000Z--d475f295562d0052ccc2b5cf0576e03c1d1e852a.json"
        );
    }

    ContractGasProvider contractGasProvider = new DefaultGasProvider();

    private VotingSystem LoadContract(String contractAddress, Web3j web3j, Credentials credentials) {
        return VotingSystem.load(contractAddress, web3j, credentials, contractGasProvider);
    }

    int deleted_candidate_id = 0;
    static int itemtoremove = 0;
    RecyclerView recyclerView;


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void enableSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleted_candidate_id = position;


                if (direction == ItemTouchHelper.LEFT) {

                    itemtoremove = ids.get(position);
                    Candidates_adapter.removeItem(position);
                    Log.d("idddddd11111", String.valueOf(ids));
                    MainActivity.DeleteDataAsync dda = new MainActivity.DeleteDataAsync();
                    dda.execute("");

                }
                if (direction == ItemTouchHelper.RIGHT)
                {
                    itemtoremove = ids.get(position);
                    Intent editactivity = new Intent(MainActivity.this,ModifyCandidateActivity.class);
                    startActivity(editactivity);
                }
            }

            ///////////////////////////////////////////////////////////////////////////////////////

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                    if(dX > 0)
                    {
                        p.setColor(Color.parseColor("#54B948"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(),dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static boolean deletingfinished=false;
    ProgressDialog progressDialog;

    public class DeleteDataAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/0642d3f26469482c984b589d336fcc80"));
            String PRIVATE_KEY = "BFE15071308C18FFF3AA08E27091530A6F8757F9D07A1A273592B2041F1E1412";
            Credentials credentials = null;
            credentials = Credentials.create(PRIVATE_KEY);

            ContractGasProvider contractGasProvider = new DefaultGasProvider();
            VotingSystem votingSystem = LoadContract(Contracts_Adapter.Election_Contract_Address, web3j, credentials);

            votingSystem.setGasProvider(contractGasProvider);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    setProgressDialog(true);

                }
            });


            try {
                votingSystem.removeCandidate(BigInteger.valueOf(itemtoremove)).send();

                setProgressDialog(false);

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
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
            tvText.setText("Deleting ...");
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