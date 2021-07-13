package com.example.note;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.CollapsibleActionView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class noteActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference noteBook = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes");

    ArrayList<firebasemodel> lists;
     static ExampleAdapter adapter;
     RecyclerView recyclerView;
     EditText editText;
     FloatingActionButton mcreateNotes;





    @Override
    protected void onCreate(Bundle  savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        getSupportActionBar().setTitle("All Notes");

        lists = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        mcreateNotes = findViewById(R.id.createNotes);

        mcreateNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(noteActivity.this,createNotes.class));
            }
        });

        editText = findViewById(R.id.searchEditText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                   filter(s.toString());
            }
        });
        int check = getIntent().getIntExtra("check",0);
        if(check==1)
        {
            String title = getIntent().getStringExtra("title");
            String content = getIntent().getStringExtra("description");
            String id = getIntent().getStringExtra("id");

            undo(title,content,id);
        }


    }

    private  void filter(String text)
    {
        ArrayList<firebasemodel> filteredList = new ArrayList<>();

        for(firebasemodel item : lists)
        {
            if(item.getTitle().toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteBook.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable  QuerySnapshot value, @Nullable  FirebaseFirestoreException error) {
                if(error!=null)
                {
                    error.printStackTrace();
                    return;
                }
                lists=new ArrayList<>();
                for(QueryDocumentSnapshot documentSnapshot:value)
                {
                    firebasemodel note = documentSnapshot.toObject(firebasemodel.class);
                    note.setDocumentID(documentSnapshot.getId());
                    lists.add(note);
                }

                adapter=new ExampleAdapter(lists,noteActivity.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(noteActivity.this));

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);
            }
        });
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN|
            ItemTouchHelper.START| ItemTouchHelper.END,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(lists,fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {

            firebasemodel note = adapter.Document(viewHolder.getAdapterPosition());
            String documentID = note.getDocumentID();
            String title = note.getTitle();
            String time = note.getTime();
            String content = note.getContent();

            firebaseFirestore.collection("notes")
                    .document(firebaseUser.getUid()).collection("myNotes").document(documentID).delete();




            DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("Archive").document();

          ArchiveBaseModel map = new ArchiveBaseModel(title,time,content);
          documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                  Toast.makeText(noteActivity.this, "Archived", Toast.LENGTH_SHORT).show();
              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull @NotNull Exception e) {
                  Toast.makeText(noteActivity.this, "Failed", Toast.LENGTH_SHORT).show();
              }
          });
        }

        @Override
        public void clearView(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(),R.color.black ));
        }

        @Override
        public void onSelectedChanged(@Nullable @org.jetbrains.annotations.Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if(actionState==ItemTouchHelper.ACTION_STATE_DRAG){
                viewHolder.itemView.setBackgroundColor(Color.CYAN);
            }
        }
    };






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(noteActivity.this,MainActivity.class));
                finish();

            case R.id.ascen:
                showassc();


            case R.id.descen:
                showdesc();

            case R.id.archive:
                Intent arch = new Intent(noteActivity.this,Archive.class);
                startActivity(arch);


        }
        return super.onOptionsItemSelected(item);
    }

    public void showassc()
    {
        firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title",Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                  lists = new ArrayList<>();
                  for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                  {
                      firebasemodel note = documentSnapshot.toObject(firebasemodel.class);
                      note.setDocumentID(documentSnapshot.getId());
                      lists.add(note);
                  }

                adapter=new ExampleAdapter(lists,noteActivity.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(noteActivity.this));
            }
        });
    }

    public void showdesc()
    {
        firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title",Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                lists = new ArrayList<>();
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    firebasemodel note = documentSnapshot.toObject(firebasemodel.class);
                    note.setDocumentID(documentSnapshot.getId());
                    lists.add(note);
                }

                adapter=new ExampleAdapter(lists,noteActivity.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(noteActivity.this));
            }
        });
    }

    public void undo(String title,String desc,String id)
    {
        Snackbar snackBar = Snackbar .make(findViewById(android.R.id.content),"Note is Removed!", Snackbar.LENGTH_LONG) .setAction("undo", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebasemodel note = new firebasemodel(title,desc,id);
                noteBook.add(note);
            }
        });
        snackBar.setActionTextColor(Color.BLACK);
        snackBar.show();
    }


}