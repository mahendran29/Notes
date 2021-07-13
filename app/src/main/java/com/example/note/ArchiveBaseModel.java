package com.example.note;

import com.google.firebase.firestore.Exclude;

public class ArchiveBaseModel
{
    String title;
    String time;
    String content;
    String documentID;


    ArchiveBaseModel()
    {

    }

    public ArchiveBaseModel(String title, String time,String content) {
        this.title = title;
        this.time = time;
        this.content=content;

    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getContent(){return content;}

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
