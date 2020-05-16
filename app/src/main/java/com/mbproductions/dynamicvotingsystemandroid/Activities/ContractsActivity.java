package com.mbproductions.dynamicvotingsystemandroid.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.mbproductions.dynamicvotingsystemandroid.Contract.Contracts_DB;
import com.mbproductions.dynamicvotingsystemandroid.R;
import com.mbproductions.dynamicvotingsystemandroid.RecyclerView.Adapter;
import com.mbproductions.dynamicvotingsystemandroid.RecyclerView.Contracts_Adapter;
import com.mbproductions.dynamicvotingsystemandroid.utils.Constants;
import com.mbproductions.dynamicvotingsystemandroid.utils.PreferenceUtils;

import org.web3j.abi.datatypes.Int;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class ContractsActivity extends AppCompatActivity {

    static ProgressBar progressBar;
    static TextView loadingText;
    static TextView nameText;
    static TextView addressText;
    TextInputLayout password_txt;
    static Button enter_btn;
    FloatingActionButton fab;

    static String newcontract_name;

    boolean granted_access = false;

    Contracts_Adapter Contracts_adapter = null;
    private Paint p = new Paint();

    Handler handler;
    Runnable runnable;

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(210000L);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

    static RecyclerView recyclerView;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contracts);

        Toast.makeText(this, PreferenceUtils.getPK(this), Toast.LENGTH_SHORT).show();
        if(PreferenceUtils.getPK(this).equals("BFE15071308C18FFF3AA08E27091530A6F8757F9D07A1A273592B2041F1E1412")) {
            isadmin=true;
            init();
            enter_btn();
        }
        else{
            init();
            runTask1();
        }

        recyclerView = findViewById(R.id.rv_contracts);

        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorFABbgTint)));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(ContractsActivity.this, NewContractActivity.class);
                startActivity(a);
            }
        });

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static boolean isadmin = false;

    private void init() {
        Toast.makeText(this,PreferenceUtils.getPK(this),Toast.LENGTH_SHORT);
        password_txt = findViewById(R.id.pass_input);
        enter_btn = findViewById(R.id.enter_button);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_contracts);
        loadingText = (TextView) findViewById(R.id.loadingtext_contracts);
        fab = findViewById(R.id.fab);

        if(isadmin!=true)
        {
            password_txt.setVisibility(View.GONE);
            enter_btn.setVisibility(View.GONE);

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                }
            };
            handler.postDelayed(runnable, 3000);
        }
        else {
            progressBar.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
        }

        fab.setVisibility(View.GONE);

    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void enter_btn() {
        enter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = String.valueOf(password_txt.getEditText().getText());
                if (a.equals("admin1")) {
                    PreferenceUtils.savePK("BFE15071308C18FFF3AA08E27091530A6F8757F9D07A1A273592B2041F1E1412", ContractsActivity.this);
                    password_txt.setVisibility(View.GONE);
                    enter_btn.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    loadingText.setVisibility(View.VISIBLE);

                    handler = new Handler();
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                        }
                    };
                    handler.postDelayed(runnable, 3000);

                    runTask1();  //check wether internet connection is available and run async task from there before loading data
                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ContractGasProvider contractGasProvider = new DefaultGasProvider();


    private Contracts_DB LoadContract(String contractAddress, Web3j web3j, Credentials credentials) {
        return Contracts_DB.load(contractAddress, web3j, credentials, contractGasProvider);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void runTask1() {
        if (haveNetworkConnection()) {
            ContractsActivity.LoadDataAsync lda = new ContractsActivity.LoadDataAsync();
            lda.execute(""); //to get number of contracts

            if (lda.getStatus() == ContractsActivity.LoadDataAsync.Status.FINISHED) {
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

    static String CONTRACT_ADDRESS = "0xcdcaa673a80e07d144e0bc7b7c76332f95774cac"; //"0xf4d0955c4c7f9706a8c96c096065296b6caa5f94"; //Contract holding contracts

    static ArrayList<Integer> ids = new ArrayList<Integer>();

    ArrayList<String> names = new ArrayList<String>();
    static ArrayList<String> addresses = new ArrayList<String>();
    int itemtoremove=0;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public class LoadDataAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/0642d3f26469482c984b589d336fcc80"));
            String PRIVATE_KEY = "BFE15071308C18FFF3AA08E27091530A6F8757F9D07A1A273592B2041F1E1412";
            Credentials credentials = null;
            credentials = Credentials.create(PRIVATE_KEY);
            Contracts_DB contracts_db = LoadContract(CONTRACT_ADDRESS, web3j, credentials);
            Log.d("deployed address: ", contracts_db.getContractAddress());
            BigInteger contractsnumber = null;
            try {
                contractsnumber = contracts_db.contractsNumber().send();
                Log.d("contractsnummmmmmm",contractsnumber.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //candidates number is a Bigint (candidates takes big int as param)
            //a is an int (cannot apply <= to a Big int)
            int a = contractsnumber.intValue();
            //int i=1;

                        ////////////////////////////////////////////////////////////////////
            addresses.clear();
            ids.clear();

            for (int i = 1; i <= a; i++) {
                RemoteCall<Tuple3<BigInteger, String, String>> contract = null;
                try {
                    contract = contracts_db.contracts(BigInteger.valueOf(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Log.d("cccccc", String.valueOf(contract.send()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int id = 0;
                String name = "";
                String address = "";
                try {
                    id = contract.send().getValue1().intValue(); //get value of id from tuple
                    if (id != 0) {
                        name = contract.send().getValue2(); //get value of name from tuple
                        address = contract.send().getValue3(); //get value of name from tuple
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (id != 0) {
                    ids.add(id);
                    Log.d("idddddd", String.valueOf(ids));
                    names.add(name);
                    addresses.add(address);
                }
            }

                        //////////////////////////////////////////////////////


            final String addr = credentials.getAddress();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    Log.d("cnamesss", String.valueOf(names.size()));
                    Contracts_adapter = new Contracts_Adapter(ContractsActivity.this, names, addresses,ids);
                    Log.d("namesssssss", String.valueOf(names));
                    Log.d("adressess22222", String.valueOf(addresses));
                    Log.d("idsssssss44444", String.valueOf(ids));
                    recyclerView.setAdapter(Contracts_adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ContractsActivity.this));

                    if(isadmin)
                    {
                        enableSwipe();
                        fab.setVisibility(View.VISIBLE);
                    }

                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);

                }
            });


            return null;
        }


        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    }

    int deleted_contract_id = 0;

    private void enableSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleted_contract_id= position;


                if (direction == ItemTouchHelper.LEFT) {

                    itemtoremove = ids.get(position);
                    Contracts_adapter.removeItem(position);

                    DeleteDataAsync dda = new DeleteDataAsync();
                    dda.execute("");

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
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public class DeleteDataAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    setProgressDialog(true);

                }
            });

            Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/0642d3f26469482c984b589d336fcc80"));
            String PRIVATE_KEY = "BFE15071308C18FFF3AA08E27091530A6F8757F9D07A1A273592B2041F1E1412";
            Credentials credentials = null;
            credentials = Credentials.create(PRIVATE_KEY);

            ContractGasProvider contractGasProvider = new DefaultGasProvider();
            Contracts_DB contracts_db = LoadContract(ContractsActivity.CONTRACT_ADDRESS, web3j, credentials);

            contracts_db.setGasProvider(contractGasProvider);

            try {

                contracts_db.removeContract(BigInteger.valueOf(itemtoremove)).send();
                Log.d("debuggggg22222", "hereeee");
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


