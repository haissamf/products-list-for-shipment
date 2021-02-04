package com.example.itemslist.Adapters;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.itemslist.MainActivity;
import com.example.itemslist.R;
import com.example.itemslist.model.Item;

import java.util.ArrayList;


// some times it just doesn't work. Just change the class name and return it!
public class RecyclerViewAdapter extends RecyclerView.Adapter< RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerAdapter";
    ArrayList<Item> items;
    Context context;
    DeleteItemListener deleteItemListener;
    EditItemListener editItemListener;


    public interface DeleteItemListener{
         void onDelete( Item item);
    }

    public interface EditItemListener{
        void onEdit( Item item);
    }

    public RecyclerViewAdapter(Context context, ArrayList< Item> items){
        Log.d(TAG, "RecyclerAdapter: " + "created");
        this.context = context;
        this.items = items;

        //set the mainActivity as a reference for the interfaces
        deleteItemListener = ( DeleteItemListener) context;
        editItemListener = ( EditItemListener) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.row, parent, false);
            return new ViewHolder( view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: binding");

        final Item temp = items.get( position);

        Log.d(TAG, "onBindViewHolder: id " + temp.getId());

        holder.name.setText( temp.getName());
        holder.quantity.setText( "Quantity: " + temp.getQuantity());
        holder.weight.setText( "Weight: " +  temp.getWeight());
        holder.size.setText( "Size: " + temp.getSize());
        holder.price.setText( "Price: " + temp.getPrice() + " $");

        holder.imageView.setImageBitmap( BitmapFactory.decodeByteArray( temp.getImage(), 0,  temp.getImage().length));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    deleteItemListener.onDelete(temp);
                }catch ( Exception e){
                    Log.d(TAG, "onClick: " + e.getMessage());
                }
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + "edit called");
                try {

                    editItemListener.onEdit(temp);
                }catch ( ClassCastException e){
                    Log.d(TAG, "onClick: exception" + e.getMessage());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + items.size());
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, quantity, weight, size, price;
        ImageView imageView;
        Button edit, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById( R.id.name_show);
            quantity = itemView.findViewById( R.id.quantity_show);
            weight = itemView.findViewById( R.id.weight_show);
            size = itemView.findViewById( R.id.size_show);
            price = itemView.findViewById( R.id.show_price_text);

            imageView = itemView.findViewById( R.id.image_show);

            edit = itemView.findViewById( R.id.edit_button);
            delete = itemView.findViewById( R.id.delete_button);
        }
    }

    public void updateList(ArrayList< Item> items){
        this.items = items;

        notifyDataSetChanged();
    }
}
