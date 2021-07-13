package com.example.note;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Archive extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference noteArchive = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("Archive");


    ArrayList<ArchiveBaseModel> archiveLists;
    static ArchiveAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        recyclerView = findViewById(R.id.archiverecyclerView);

    }

    @Override
    protected void onStart() {
        super.onStart();

        noteArchive.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable  QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    error.printStackTrace();
                    return;
                }
                archiveLists=new ArrayList<>();
                for(QueryDocumentSnapshot documentSnapshot:value)
                {
                    ArchiveBaseModel note = documentSnapshot.toObject(ArchiveBaseModel.class);
                    note.setDocumentID(documentSnapshot.getId());
                    archiveLists.add(note);
                }

                adapter=new ArchiveAdapter(archiveLists,Archive.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(Archive.this));
            }
        });










    }
}