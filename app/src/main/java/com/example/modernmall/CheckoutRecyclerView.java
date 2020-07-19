package com.example.modernmall;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class CheckoutRecyclerView extends RecyclerView.Adapter<CheckoutRecyclerView.ViewHolder> {
    private ArrayList<String> item;
    private ArrayList<String> unitPrice;
    private ArrayList<String> quantity;
    private ArrayList<String> totalPrice;
    public CheckoutRecyclerView(  ArrayList<String> item, ArrayList<String> unitPrice, ArrayList<String> quantity, ArrayList<String> totalPrice)
    {
        this.item=item;
        this.unitPrice=unitPrice;
        this.quantity=quantity;
        this.totalPrice=totalPrice;
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
    public CheckoutRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cv=(CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_cardview_layout,viewGroup,false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutRecyclerView.ViewHolder viewHolder, int i) {
        final CardView cw=viewHolder.cardView;
        TextView textView1=(TextView)cw.findViewById(R.id.ccl_textview1);
        TextView textView2=(TextView)cw.findViewById(R.id.ccl_textview2);
        TextView textView3=(TextView)cw.findViewById(R.id.ccl_textview3);
        TextView textView4=(TextView)cw.findViewById(R.id.ccl_textview4);

        textView1.setText(item.get(i));
        textView2.setText("Unit Cost: ₹"+unitPrice.get(i));
        textView3.setText("Count: "+quantity.get(i));
        textView4.setText("Total Cost: ₹"+totalPrice.get(i));
    }

    @Override
    public int getItemCount() {
        return item.size();
    }
}
