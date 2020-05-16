package com.mbproductions.dynamicvotingsystemandroid.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mbproductions.dynamicvotingsystemandroid.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

  private static final String TAG="RecyclerViewAdapter";

    private ArrayList<Integer> CVotes = new ArrayList<>();
  private ArrayList<String> CNames = new ArrayList<>();
    private int[] images;
  //private ArrayList<String> Images = new ArrayList<>();
  private Context mContext;
    private SparseBooleanArray selectedItems;
    public static int candidateIDselected=0;
    private ArrayList<Integer> Cids = new ArrayList<>();
    private boolean gender;

    public Adapter(Context mContext,ArrayList<Integer> Cids, ArrayList<String> CNames, ArrayList<Integer> CVotes,boolean gender, int[]images) { //constructor (ALT+SHIFT+NSERT)
        this.CNames = CNames;
        this.CVotes=CVotes;
        //Images = images;
        this.mContext = mContext;
        this.images=images;
        this.gender=gender;
        this.Cids=Cids;
    }

    public void removeItem(int position) {
        CNames.remove(position);
        CVotes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, CNames.size());

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.model,parent,false);
        ViewHolder holder = new ViewHolder(view);
        selectedItems = new SparseBooleanArray();
        return holder;
    }

    public void selectLastItem(int pos){  //keeps track of which item is selected to change colors
        selectedItems.clear();
        selectedItems.put(pos, true);
        candidateIDselected=Cids.get(pos); //keep track of selected candidate
        Log.d("candidate selected", String.valueOf(candidateIDselected));
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.CandidateName.setText(CNames.get(position));

        int image_id=images[position];

        holder.CandidateImage.setImageResource(image_id);
        holder.CandidateSumVotes.setText("Votes: " + CVotes.get(position));
        holder.linearLayout.setBackgroundColor(selectedItems.get(position, false) ?
                ResourcesCompat.getColor(holder.linearLayout.getResources(),
                        R.color.holo_blue_dark, null) : Color.parseColor("#C6DEFF"));

        //String b = String.valueOf(selectedItems.keyAt(position));
        //Log.d("color change", b);

        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, CNames.get(position), Toast.LENGTH_SHORT).show();
                selectLastItem(position);


            }
        });

    }

    @Override
    public int getItemCount() {
        return CNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

      ImageView CandidateImage;
      TextView CandidateName;
      TextView CandidateSumVotes;
      RelativeLayout parent_layout;
      LinearLayout linearLayout;
      public ViewHolder(View itemView) {
          super(itemView);
          CandidateImage = itemView.findViewById(R.id.CandidatePicture);
          CandidateName = itemView.findViewById(R.id.CandidateName);
          CandidateSumVotes = itemView.findViewById(R.id.CandidateVoteCount);
          parent_layout= itemView.findViewById(R.id.parent_layout);
          linearLayout=itemView.findViewById(R.id.linlayout);
      }
  }

/*    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView CandidatePicture;
        TextView CandidateName;
        TextView CandidateSumVotes;

        public ImageViewHolder(View itemView) {
            super(itemView);
            CandidateImage = itemView.findViewById(R.id.CandidatePicture);
            CandidateName = itemView.findViewById(R.id.CandidateName);
            CandidateSumVotes = itemView.findViewById(R.id.CandidateVoteCount);
        }
    }*/
}
