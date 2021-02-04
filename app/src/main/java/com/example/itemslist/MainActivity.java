package com.example.itemslist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.itemslist.Adapters.RecyclerViewAdapter;
import com.example.itemslist.data.DataBaseHandler;
import com.example.itemslist.model.Item;
import com.example.itemslist.model.ValuesChangedForPriceListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.DeleteItemListener
        , RecyclerViewAdapter.EditItemListener, View.OnFocusChangeListener, View.OnKeyListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_STORAGE_ENTER = 111;
    private static final int REQUEST_CODE_STORAGE_EDIT = 222;


    DataBaseHandler db;
    ArrayList<Item> items ;
    AlertDialog.Builder builderAdd;
    AlertDialog dialogAdd;
    AlertDialog.Builder builderEdit;
    AlertDialog dialogEdit;
    EditText name, quantity, weight, size;
    EditText nameEdit, quantityEdit, weightEdit, sizeEdit;
    TextView price, priceEdit, totalPrice;
    Button add;
    Button applyButton;
    ImageView image, imageViewEdit;

    FloatingActionButton fab;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;

    View tempView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        fab = findViewById(R.id.fab);
        fab.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                createAlertDialog();
//                startRepositionAnimation( view);
            }
        });

        db = new DataBaseHandler( this);
        items = db.getAllItems();

        for( Item temp: items)
            Log.d( "Items: " , "" + temp);

        totalPrice = findViewById( R.id.total_price);
        recyclerView = findViewById( R.id.recyclerView);
        adapter = new RecyclerViewAdapter(this, items);
        recyclerView.setAdapter( adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalPrice.setText("Total Price: " + ( float) Math.round(getTotal() * 100)  / 100 + "$");
    }

    private void createAlertDialog() {


        View view = getLayoutInflater().inflate( R.layout.popup, null);

        tempView = view;
        builderAdd = new AlertDialog.Builder( this);
        builderAdd.setView( view);

        dialogAdd = builderAdd.create();
        dialogAdd.show();

        name = view.findViewById( R.id.name_enter);
        quantity = view.findViewById( R.id.quantity_enter);
        weight = view.findViewById( R.id.weight_enter);
        size = view.findViewById( R.id.size_enter);
        price = view.findViewById( R.id.add_price);
        image = view.findViewById( R.id.image);
        add = view.findViewById( R.id.add_button);


        quantity.setOnKeyListener( this);
        weight.setOnKeyListener(this);
        size.setOnKeyListener(this);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d(TAG, "onCreate: accessing");
                    requestPermissions( new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_ENTER);
                }
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View view) {

                if( !name.getText().equals("") &&
                        !quantity.getText().toString().equals("")&&
                        !weight.getText().toString().equals("")&&
                        !size.getText().toString().equals("")
                        && image.getDrawable() != null) {

                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    ByteArrayOutputStream boas = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
                    byte[] temp = boas.toByteArray();

                    addItem(new Item(name.getText().toString(),
                            Integer.parseInt(quantity.getText().toString()),
                            weight.getText().toString(),
                            Integer.parseInt(size.getText().toString())
                            , temp, (float) Math.round(getPrice(Integer.parseInt(quantity.getText().toString()),
                            Integer.parseInt(weight.getText().toString()),
                            Integer.parseInt(size.getText().toString())) * 100) / 100 ));

                    Log.d(TAG, "run: " + "finished");

                    dialogAdd.dismiss();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //prepare adding the image
//
//                    }
//                }, 2000);
                    totalPrice.setText("Total Price: " + ( float) Math.round(getTotal() * 100)  / 100 + "$");

                }
                else
                {
                    Snackbar.make( view, "Fill all parts!", 1000).show();
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onRequestPermissionsResult: permission is granted");
            Intent intent = new Intent( Intent.ACTION_PICK);
            intent.setType( "image/*");

            if( requestCode == REQUEST_CODE_STORAGE_ENTER)
                startActivityForResult( intent, REQUEST_CODE_STORAGE_ENTER);

            else if(  requestCode == REQUEST_CODE_STORAGE_EDIT)
                startActivityForResult( intent, REQUEST_CODE_STORAGE_EDIT);
        }
        else{
            Log.d(TAG, "onRequestPermissionsResult: do not have permission");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK & data != null){
            Uri uri = data.getData();

            try {
                Log.d(TAG, "onActivityResult: setting image");
                InputStream inputStream = getContentResolver().openInputStream( uri);
                Bitmap bitmap = BitmapFactory.decodeStream( inputStream);

                if( requestCode == REQUEST_CODE_STORAGE_ENTER )
                    image.setImageBitmap( bitmap);
                else if( requestCode == REQUEST_CODE_STORAGE_EDIT)
                    imageViewEdit.setImageBitmap( bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addItem( Item item){
        Log.d(TAG, "addItem: " + item);

        db.addItem( item);

        items = db.getAllItems();

        for( Item temp: items)
            Log.d(TAG, "addItem: " + temp);

        adapter.updateList( items);
    }

    @Override
    public void onDelete(Item item) {
        Log.d(TAG, "onDelete: " + item);

        db.deleteItem( item);
        items = db.getAllItems();
        adapter.updateList( items);

        totalPrice.setText("Total Price: " + ( float) Math.round(getTotal() * 100)  / 100 + "$");
    }


    @Override
    public void onEdit(final Item item) {
        Log.d(TAG, "onEdit: " + item);



        View view = getLayoutInflater().inflate( R.layout.popup_edit, null, false);

        nameEdit = view.findViewById( R.id.name_edit);
        quantityEdit = view.findViewById( R.id.quantity_edit);
        weightEdit = view.findViewById( R.id.weight_edit);
        sizeEdit = view.findViewById( R.id.size_edit);
        priceEdit = view.findViewById( R.id.edit_price);
        imageViewEdit = view.findViewById( R.id.image_edit);
        applyButton = view.findViewById( R.id.apply_button);


        nameEdit.setText( item.getName());
        quantityEdit.setText(  "" + item.getQuantity(),  TextView.BufferType.EDITABLE);
        weightEdit.setText( "" + item.getWeight(), TextView.BufferType.EDITABLE);
        sizeEdit.setText( "" + item.getSize(), TextView.BufferType.EDITABLE);
        imageViewEdit.setImageBitmap( BitmapFactory.decodeByteArray( item.getImage(), 0, item.getImage().length));

        quantityEdit.setOnKeyListener( this);
        weightEdit.setOnKeyListener(this);
        sizeEdit.setOnKeyListener(this);

        quantityEdit.setOnFocusChangeListener( this);
        weightEdit.setOnKeyListener(this);
        sizeEdit.setOnKeyListener(this);

        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                requestPermissions( new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,}, REQUEST_CODE_STORAGE_EDIT);
            }
        });

        if( applyButton == null)
            Log.d(TAG, "onEdit: apply button is null");

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( !nameEdit.getText().equals("") &&
                        !quantityEdit.getText().toString().equals("")&&
                        !weightEdit.getText().toString().equals("")&&
                        !sizeEdit.getText().toString().equals("")
                        && imageViewEdit.getDrawable() != null) {
                    //prepare the image
                    Bitmap bitmap = ((BitmapDrawable) imageViewEdit.getDrawable()).getBitmap();
                    ByteArrayOutputStream boas = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
                    final byte[] temp = boas.toByteArray();

                    Log.d(TAG, "onClick: apply id " + item.getId());

                    Item editedItem = new Item(nameEdit.getText().toString(),
                            Integer.parseInt(quantityEdit.getText().toString()),
                            weightEdit.getText().toString(),
                            Integer.parseInt(sizeEdit.getText().toString())
                            , temp, (float) Math.round(getPrice(Integer.parseInt(quantityEdit.getText().toString()),
                            Integer.parseInt(weightEdit.getText().toString()),
                            Integer.parseInt(sizeEdit.getText().toString())) * 100) / 100, item.getId());

                    Log.d("Edit,: ", "" + editedItem);
                    db.updateItem(editedItem);

                    items = db.getAllItems();
                    adapter.updateList(items);
                    for (Item t : items)
                        Log.d("Items: ", "" + t);
                    dialogEdit.dismiss();

                    totalPrice.setText("Total Price: " + ( float) Math.round(getTotal() * 100)  / 100 + "$");
                }
                else
                {
                    Snackbar.make( view, "All parts must NOT be empty!", 1000).show();
                }

            }
        });
        //create the popup
        builderEdit = new AlertDialog.Builder( this);
        builderEdit.setView( view);
        dialogEdit = builderEdit.create();
        dialogEdit.show();

        //show price
            priceEdit.setText( "Price: " + ( float ) Math.round( getPrice(Integer.parseInt(quantityEdit.getText().toString()),
                    Integer.parseInt(weightEdit.getText().toString()),
                    Integer.parseInt(sizeEdit.getText().toString()))  * 100) / 100  + " $" );
        }

    public float getPrice( int quantity, int weight, int size) {
        final float WEIGHT_RATE  = .07f ;
        final float SIZE_RATE  = .03f ;

        if( quantity > 0 && weight > 0 && size > 0 )
            return quantity * (weight * WEIGHT_RATE + size * SIZE_RATE);
        return 0;
    }

    public float getTotal(){
        float total;
        total = 0;

        for( Item temp : items)
            total = total + temp.getPrice();
        return total;
    }

    @Override
    public void onFocusChange(View view, boolean b) {

        onValueChanged( view);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        onValueChanged( view);
        return false;
    }

    public void onValueChanged( View view){

        if (view.getId() == R.id.quantity_edit || view.getId() == R.id.size_edit || view.getId() == R.id.weight_edit) {
            Log.d(TAG, "onKey: Edit id");
            if (!quantityEdit.getText().toString().equals("") && !weightEdit.getText().toString().equals("") && !sizeEdit.getText().toString().equals("")) {
                Log.d(TAG, "onKey: !all empty   ");
                priceEdit.setText("Price: " + (float) Math.round(getPrice(Integer.parseInt(quantityEdit.getText().toString()),
                        Integer.parseInt(weightEdit.getText().toString()),
                        Integer.parseInt(sizeEdit.getText().toString())) * 100) / 100 + " $");
            }
            else
                priceEdit.setText("Price: ");

        }

        else if (view.getId() == R.id.quantity_enter || view.getId() == R.id.size_enter || view.getId() == R.id.weight_enter) {

            if (!quantity.getText().toString().equals("") && !weight.getText().toString().equals("") && !size.getText().toString().equals("")) {

                price.setText("Price: " + (float) Math.round(getPrice(Integer.parseInt(quantity.getText().toString()),
                        Integer.parseInt(weight.getText().toString()),
                        Integer.parseInt(size.getText().toString())) * 100) / 100 + " $");
            }
            else
                price.setText("Price: ");
        }

    }


}