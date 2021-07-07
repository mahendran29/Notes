package com.example.note;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder>
{
    ArrayList<firebasemodel> listItems;
    Context context;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    public ExampleAdapter(ArrayList<firebasemodel> mlistitems, Context context)
    {
        listItems=mlistitems;
        this.context=context;
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
        return  new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleAdapter.ExampleViewHolder holder, int position)
    {
        firebasemodel note = listItems.get(position);
        holder.textView.setText(note.getTitle());
        holder.calendar.setText("Created on: "+note.getTime());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = firestore.collection("notes")
                        .document(firebaseUser.getUid()).collection("myNotes").document(note.getDocumentID());

                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        noteActivity.adapter.notifyItemRemoved(position);
                        firebasemodel note = listItems.get(position);

                        Intent intent3 = new Intent(context,noteActivity.class);
                        intent3.putExtra("check",1);
                        intent3.putExtra("title",note.getTitle());
                        intent3.putExtra("description",note.getContent());
                        intent3.putExtra("id",note.getTime());
                        context.startActivity(intent3);


                    }
                });

            }
        });

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,content.class);

                firebasemodel note = listItems.get(position);

                intent.putExtra("title",note.getTitle());
                intent.putExtra("description",note.getContent());
                intent.putExtra("id",note.getDocumentID());

                context.startActivity(intent);
            }
        });


    }



    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void filterList(ArrayList<firebasemodel> filteredList)
    {
        listItems = filteredList;
        notifyDataSetChanged();
    }

    public  static  class ExampleViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView;
        CardView cardView;
        ImageView imageView;
        TextView calendar;
        public ExampleViewHolder(@NonNull  View itemView) {
            super(itemView);

            textView=itemView.findViewById(R.id.noteTitle);
            cardView=itemView.findViewById(R.id.notecard);
            imageView=itemView.findViewById(R.id.delete);
            calendar=itemView.findViewById(R.id.Timecalendar);
        }
    }

}
