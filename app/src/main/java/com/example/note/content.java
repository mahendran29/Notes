package com.example.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class content extends AppCompatActivity {

    TextView titleTextView;
    TextView content;
    FloatingActionButton floatingActionButton;

    String title,description,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        getSupportActionBar().setTitle("Content");

        titleTextView = findViewById(R.id.title);
        content = findViewById(R.id.content);
        floatingActionButton=findViewById(R.id.editNotes);

        Intent intent = getIntent();

         title = intent.getStringExtra("title");
         description = intent.getStringExtra("description");
         id = intent.getStringExtra("id");

        titleTextView.setText(title);
        content.setText(description);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(content.this,editActivity.class);
                intent1.putExtra("title",title);
                intent1.putExtra("content",description);
                intent1.putExtra("id",id);

                startActivity(intent1);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.contentmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.share:
                noteShare();
                break;

//            case R.id.reminder:
//                reminder();
//                break;



        }
        return super.onOptionsItemSelected(item);
    }

    private void noteShare() {

        Intent share = new Intent(Intent.ACTION_SEND);
        String note;
        note = "Title:\n"+title+"\n\nContent:\n"+description+" ";
        share.putExtra(Intent.EXTRA_TEXT,note);
        share.setType("text/plain");
        startActivity(share);
    }

    private void reminder()
    {

    }


}