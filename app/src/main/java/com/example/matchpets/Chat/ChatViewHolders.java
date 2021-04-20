package com.example.matchpets.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matchpets.R;

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMessaage;
    public LinearLayout mContainer;
    public ChatViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMessaage = itemView.findViewById(R.id.msg);
        mContainer = itemView.findViewById(R.id.container);
    }

    @Override
    public void onClick(View v) {

    }
}
