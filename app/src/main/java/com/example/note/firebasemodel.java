package com.example.note;

import com.google.firebase.firestore.Exclude;

public class firebasemodel
{
    private String title;
    private String content;



    public firebasemodel()
    {

    }

    public firebasemodel(String title,String content)
    {
        this.title=title;
        this.content=content;
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    private String documentID;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
