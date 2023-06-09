package com.example.koteki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView twc;
    DatabaseAdapter databaseAdapter;
    ProfileAdapter profileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.list_item);
        twc = findViewById(R.id.ttv);

        ItemTouchHelper touchHelper = new ItemTouchHelper(simpleCallback);
        touchHelper.attachToRecyclerView(recyclerView);
    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            switch (direction){
                case ItemTouchHelper.LEFT:{
                    Profile deletedProfile= databaseAdapter.getSingleProfile((long) viewHolder.itemView.getTag());
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Deleting")
                            .setMessage("Delete " + deletedProfile.name + "?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    databaseAdapter.delete(deletedProfile._id);
                                    onResume();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onResume();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    break;
                }
                case ItemTouchHelper.RIGHT:{
                    editProfile(recyclerView, (long)viewHolder.itemView.getTag());
                    break;
                }
            }
        }
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    //.addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.my_background))
                    .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                    .addSwipeRightActionIcon(R.drawable.baseline_edit_24)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    protected void onResume() {
        databaseAdapter = new DatabaseAdapter(MainActivity.this);
        super.onResume();
        databaseAdapter.open();
        profileAdapter = new ProfileAdapter(MainActivity.this, databaseAdapter.profiles());
        recyclerView.setAdapter(profileAdapter);
        twc.setText("Найдено элементов: " + profileAdapter.getItemCount());
        profileAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void addProfile(View view) {
        Dialog addDialog = new Dialog(this, R.style.Theme_Koteki);
        addDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100,0,0,0)));
        addDialog.setContentView(R.layout.add_profile_dialog);

        EditText dialogName = addDialog.findViewById(R.id.catName);
        EditText dialogAge = addDialog.findViewById(R.id.catAge);
        addDialog.findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = dialogName.getText().toString();
                int age = Integer.parseInt(dialogAge.getText().toString());
                databaseAdapter.insert(new Profile(name, age, R.mipmap.ic_maxwell_foreground));
                Toast.makeText(MainActivity.this, "Данные добавлены!", Toast.LENGTH_SHORT).show();
                onResume();
                addDialog.hide();
            }
        });
        addDialog.show();
    }
    public void editProfile(View view, long id) {
        Dialog editDialog = new Dialog(this, R.style.Theme_Koteki);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100,0,0,0)));
        editDialog.setContentView(R.layout.edit_profile_dialog);

        EditText dialogName = editDialog.findViewById(R.id.catName);
        EditText dialogAge = editDialog.findViewById(R.id.catAge);

        Profile profile = databaseAdapter.getSingleProfile(id);
        dialogName.setText(profile.name);
        dialogAge.setText(String.valueOf(profile.age));

        editDialog.findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = dialogName.getText().toString();
                int age = Integer.parseInt(dialogAge.getText().toString());

                databaseAdapter.update(new Profile(id, name, age, R.mipmap.ic_maxwell_foreground));
                onResume();
                Toast.makeText(MainActivity.this, "Изменения внесены!", Toast.LENGTH_SHORT).show();

                editDialog.hide();
            }
        });
        editDialog.show();
    }
}