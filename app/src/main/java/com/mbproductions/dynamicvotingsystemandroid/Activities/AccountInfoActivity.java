package com.mbproductions.dynamicvotingsystemandroid.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mbproductions.dynamicvotingsystemandroid.R;
import com.mbproductions.dynamicvotingsystemandroid.utils.PreferenceUtils;

public class AccountInfoActivity extends AppCompatActivity {

    static String PK="";
    static String MNEMONIC="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        Intent intent = getIntent();

        if (intent.hasExtra("PK")){
            PK = getIntent().getStringExtra("PK");
        }else{
            PK = PreferenceUtils.getPK(this);
            Toast.makeText(this, PK , Toast.LENGTH_SHORT).show();
        }

        if (intent.hasExtra("MNEMONIC")){
            MNEMONIC = getIntent().getStringExtra("MNEMONIC");
        }

        EditText mnemonic_txt = findViewById(R.id.mnemonic_text);
        EditText pk_txt = findViewById(R.id.pk_text);
        Button votebtn=findViewById(R.id.vote_btn);

        mnemonic_txt.setText(MNEMONIC);
        pk_txt.setText(PK);

        votebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent contracts=new Intent(AccountInfoActivity.this, ContractsActivity.class);
                startActivity(contracts);
            }
        });



    }
}
