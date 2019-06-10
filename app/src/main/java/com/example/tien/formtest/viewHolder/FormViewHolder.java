package com.example.tien.formtest.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tien.formtest.R;
import com.example.tien.formtest.iterface.ItemClickListener;

/**
 * Created by Tien on 05/01/2018.
 */

public class FormViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView formName;
    public ImageView formImage, formFavImage;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FormViewHolder(View itemView) {
        super(itemView);

        formName = (TextView) itemView.findViewById(R.id.form_name);
        formImage = (ImageView) itemView.findViewById(R.id.form_image);
        formFavImage = (ImageView) itemView.findViewById(R.id.imgFav);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);

    }
}
