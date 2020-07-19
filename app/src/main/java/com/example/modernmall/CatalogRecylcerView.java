package com.example.modernmall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class CatalogRecylcerView extends RecyclerView.Adapter<CatalogRecylcerView.ViewHolder> {
    private ArrayList<String> categories;
    private ArrayList<String> categoriesImages;
    public CatalogRecylcerView(ArrayList<String> categories,ArrayList<String>categoriesImages)
    {
        this.categories=categories;
        this.categoriesImages=categoriesImages;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView cardView)
        {
            super(cardView);
            this.cardView=cardView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cv=(CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardviewlayout,viewGroup,false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final CardView cw=viewHolder.cardView;
        ImageView imageView=(ImageView)cw.findViewById(R.id.cl_imageview1);
        TextView textView=(TextView)cw.findViewById(R.id.cl_textview1);
        textView.setText(categories.get(i));

           // imageView.setImageBitmap(categoriesImages.get(i));

        Uri uri=Uri.parse(categoriesImages.get(i));
        Picasso.get().load(uri).resize(150,150).centerCrop().into(imageView);  // .placeholder()

        cw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(cw.getContext()).edit().putString(InventoryAddRemoveActivity.
                        currentCategory,categories.get(i)).apply();
                if(PreferenceManager.getDefaultSharedPreferences(cw.getContext()).getString(LoginActivity.sellerOrCustomer,"").equals("seller")) {
                    Intent intent = new Intent(cw.getContext(), SubCatalogSellerActivity.class);
                    intent.putExtra(SubCatalogSellerActivity.intentExtra, i);
                    cw.getContext().startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(cw.getContext(), SubCatalogCustomerActivity.class);
                    PreferenceManager.getDefaultSharedPreferences(cw.getContext()).edit().putString(InventoryAddRemoveActivity.currentCategory,categories.get(i));
                    //intent.putExtra(SubCatalogCustomerActivity.intentExtra, categories.get(i));
                    cw.getContext().startActivity(intent);
                }
            }
        });
    }
}
