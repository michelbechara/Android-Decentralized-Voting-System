package com.mbproductions.dynamicvotingsystemandroid.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kenai.jffi.Main;
import com.mbproductions.dynamicvotingsystemandroid.Activities.ContractsActivity;
import com.mbproductions.dynamicvotingsystemandroid.Activities.MainActivity;
import com.mbproductions.dynamicvotingsystemandroid.R;

import java.util.ArrayList;

public class Contracts_Adapter extends RecyclerView.Adapter<Contracts_Adapter.ViewHolder> {

    private SparseBooleanArray selectedItems;
    private Context mContext;

    private ArrayList<String> CNames = new ArrayList<>();
    private ArrayList<String> CAddresses = new ArrayList<>();
    public static ArrayList<Integer> Cids = new ArrayList<>();


    public static int contractIDselected = 0;

    public Contracts_Adapter(Context mContext, ArrayList<String> CNames, ArrayList<String> CAddresses, ArrayList<Integer> Cids) { //constructor (ALT+SHIFT+NSERT)

        this.CNames = CNames;
        this.CAddresses = CAddresses;
        this.mContext = mContext;
        this.Cids = Cids;
    }

    public void removeItem(int position) {
        CNames.remove(position);
        CAddresses.remove(position);
        Cids.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, CNames.size());

    }

    public void addnotify(int position)
    {
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contract_model, parent, false);

        ViewHolder holder = new ViewHolder(view);
        selectedItems = new SparseBooleanArray();
        return holder;
    }


    public void selectLastItem(int pos){  //keeps track of which item is selected to change colors
        selectedItems.clear();
        selectedItems.put(pos, true);
        contractIDselected=pos+1; //keep track of selected candidate
        Log.d("candidate selected", String.valueOf(contractIDselected));
        notifyDataSetChanged();
    }

    public static String Election_Contract_Address="";

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.d("namesssss",CNames.get(0));
        holder.name_text.setText(CNames.get(position));
        Log.d("posssss",String.valueOf(position));
        holder.address_text.setText(CAddresses.get(position));
        Log.d("adresssss",CAddresses.get(position));
        holder.linearLayout.setBackgroundColor(selectedItems.get(position, false) ?
                ResourcesCompat.getColor(holder.linearLayout.getResources(),
                        R.color.holo_blue_dark, null) : Color.parseColor("#FFFFFF"));

        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, CAddresses.get(position), Toast.LENGTH_SHORT).show();
                selectLastItem(position);
                Election_Contract_Address=CAddresses.get(position);

                Intent MainActivity = new Intent(mContext, com.mbproductions.dynamicvotingsystemandroid.Activities.MainActivity.class);
                mContext.startActivity(MainActivity);
                //((Activity)mContext).finish();
            }
        });
    }

    @Override
    public int getItemCount() { return CNames.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name_text;
        TextView address_text;
        CardView parent_layout;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d("Kingg","Kingg");
            name_text = itemView.findViewById(R.id.name_of_contract);
            address_text = itemView.findViewById(R.id.address_of_contract);
            parent_layout = itemView.findViewById(R.id.parent_layout_contracts);
            linearLayout = itemView.findViewById(R.id.linlayout_election);
        }
    }
}
