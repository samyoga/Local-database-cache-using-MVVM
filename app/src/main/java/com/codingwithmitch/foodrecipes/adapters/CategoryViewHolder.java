package com.codingwithmitch.foodrecipes.adapters;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.foodrecipes.R;
import com.codingwithmitch.foodrecipes.models.Recipe;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    CircleImageView categoryImage;
    TextView categoryTitle;
    OnRecipeListener listener;
    RequestManager requestManager;

    public CategoryViewHolder(@NonNull View itemView, OnRecipeListener listener, RequestManager requestManager) {
        super(itemView);

        this.listener = listener;
        this.requestManager = requestManager;
        categoryImage = itemView.findViewById(R.id.category_image);
        categoryTitle = itemView.findViewById(R.id.category_title);

        itemView.setOnClickListener(this);
    }

    public void onBind(Recipe recipe){

        Uri path = Uri.parse("android.resource://com.codingwithmitch.foodrecipes/drawable/" + recipe.getImage_url());
        requestManager
                .load(path)
                .into(categoryImage);

        categoryTitle.setText(recipe.getTitle());

    }

    @Override
    public void onClick(View v) {
        listener.onCategoryClick(categoryTitle.getText().toString());
    }
}
