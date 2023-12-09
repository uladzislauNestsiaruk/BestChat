package com.example.mychat;

public class User {
    public String name;
    public String email;
    public String id;
    public int AvatarResource;
    public User(){

    }
    public User(String Name ,String Email, String Id, int AvatarResource){
        name = Name;
        email = Email;
        id = Id;
        this.AvatarResource = AvatarResource;
    }
    public String GetName(){
        return name;
    }
    public void SetName(String Name){
        name = Name;
    }
    public String GetEmail(){
        return email;
    }
    public void SetEmail(String Email){
        email = Email;
    }
    public String GetId(){
        return id;
    }
    public void SetId(String Id){
        id = Id;
    }
    public int GetAvatarResource(){
        return AvatarResource;
    }
    public void SetAvatarResource(int AvatarResource){
        this.AvatarResource = AvatarResource;
    }
}
