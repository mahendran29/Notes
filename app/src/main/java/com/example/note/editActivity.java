package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class editActivity extends AppCompatActivity {

    String id,title,desc;
    EditText noteTitle,noteContent;
    FloatingActionButton floatingActionButton;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Intent intent = getIntent();
        title=intent.getExtras().getString("title");
        desc=intent.getExtras().getString("content");
        id=intent.getExtras().getString("id");

        noteTitle=findViewById(R.id.editTitleofNotes);
        noteContent=findViewById(R.id.editContentOfNote);
        floatingActionButton = findViewById(R.id.EditsaveNotes);

        noteTitle.setText(title);
        noteContent.setText(desc);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(id).update("title",noteTitle.getText().toString());
                firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(id).update("content",noteContent.getText().toString());
                Toast.makeText(editActivity.this, "Saved", Toast.LENGTH_SHORT).show();

                 Intent intent2 = new Intent(editActivity.this,noteActivity.class);
                 startActivity(intent2);
                 finish();

            }
        });
    }


}