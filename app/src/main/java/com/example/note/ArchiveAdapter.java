package com.example.note;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ExampleViewHolder>
{
    ArrayList<ArchiveBaseModel> lists;
    Context context;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    ArchiveAdapter(ArrayList<ArchiveBaseModel> lists, Context context)
    {
         this.lists = lists;
         this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_notes_card,parent,false);
        return new ExampleViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExampleViewHolder holder, int position) {

        ArchiveBaseModel note =lists.get(position);
        holder.title.setText(note.getTitle());
        holder.time.setText(note.getTime());

        holder.restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = firebaseFirestore.collection("notes")
                        .document(firebaseUser.getUid()).collection("Archive").document(note.getDocumentID());

                String title = note.getTitle();
                String content = note.getContent();
                String time = note.getTime();

                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();

                        firebasemodel map = new firebasemodel(title,content,time);
                        documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "RESTORED", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }


    public  static  class ExampleViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        TextView time;
        ImageView restore;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);

             title = itemView.findViewById(R.id.archivenoteTitle);
             time = itemView.findViewById(R.id.archiveTimecalendar);
             restore = itemView.findViewById(R.id.archiverestore);

        }
    }
}
