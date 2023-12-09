package com.example.mychat;

public class MyChatMessage {
    public String text;
    public String name;
    public String imageUrl;
    public String GetText(){
        return text;
    }
    public void SetText(String Text){
        this.text = Text;
    }
    public String GetName(){
        return name;
    }
    public void SetName(String Name){
        this.name = Name;
    }
    public String GetImageUrl(){
        return imageUrl;
    }
    public void SetImageUrl(String ImageUrl){
        this.imageUrl = ImageUrl;
    }
    MyChatMessage(){

    }
    MyChatMessage(String Text, String Name, String ImageUrl){
        this.text = Text;
        this.name = Name;
        this.imageUrl = ImageUrl;
    }
}
