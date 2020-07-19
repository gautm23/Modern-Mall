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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdapterInventoryRecyclerView extends RecyclerView.Adapter<AdapterInventoryRecyclerView.ViewHolder> {
    private ArrayList<String> inventories;
    private ArrayList<String> inventoriesImages;
    private ArrayList<String> inventoriesQuantity;
    private ArrayList<String> inventoriesPrice;
    public AdapterInventoryRecyclerView(ArrayList<String> inventories,ArrayList<String>inventoriesImages,ArrayList<String> inventoriesQuantity,ArrayList<String> inventoriesPrice)
    {
        this.inventories=inventories;
        this.inventoriesImages=inventoriesImages;
        this.inventoriesQuantity=inventoriesQuantity;
        this.inventoriesPrice=inventoriesPrice;
    }

    @Override
    public int getItemCount() {
        return inventories.size();
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
        CardView cv=(CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inventory_cardview,viewGroup,false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final CardView cw=viewHolder.cardView;
        ImageView imageView=(ImageView)cw.findViewById(R.id.ic_imageview1);
        TextView textView1=(TextView)cw.findViewById(R.id.ic_textview1);
        TextView textView2=(TextView)cw.findViewById(R.id.ic_textview2);
        TextView textView3=(TextView)cw.findViewById(R.id.ic_textview3);
        textView1.setText(inventories.get(i));
        textView2.setText("Qty:"+inventoriesQuantity.get(i));
        textView3.setText("₹ "+inventoriesPrice.get(i)+"(pcs/kg)");
        Uri uri=Uri.parse(inventoriesImages.get(i));
        Picasso.get().load(uri).resize(150,150).centerCrop().into(imageView);  // .placeholder();
        //imageView.setImageBitmap(inventoriesImages.get(i));
        cw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(cw.getContext()).edit().putString(InventoryAddRemoveActivity.
                        currentInventory,inventories.get(i)).apply();
                if(PreferenceManager.getDefaultSharedPreferences(cw.getContext()).getString(LoginActivity.sellerOrCustomer,"").equals("customer")) {
                    Intent intent = new Intent(cw.getContext(), CartActivity.class);
                    intent.putExtra(CartActivity.cartintentstring1, inventories.get(i));
                    intent.putExtra(CartActivity.cartintentstring2, inventoriesQuantity.get(i));
                    intent.putExtra(CartActivity.cartintentstring3, inventoriesPrice.get(i));
                    cw.getContext().startActivity(intent);
                }
            }
        });
    }
}

