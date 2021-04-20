package com.example.matchpets.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matchpets.R;

import com.example.matchpets.Chat.ChatActivity;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMatchId , mMatchName;
    public ImageView mMatchImage;

    public MatchesViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mMatchId = (TextView) itemView.findViewById(R.id.matchId);
        mMatchName = (TextView) itemView.findViewById(R.id.matchName);
        mMatchImage =  (ImageView) itemView.findViewById(R.id.matchImage);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId" , mMatchId.getText().toString());
        b.putString("matchName" , mMatchName.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}
