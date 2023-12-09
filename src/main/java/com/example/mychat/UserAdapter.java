package com.example.mychat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<User> users = new ArrayList<>();
    public interface OnUserClickListener{
        void onUserClick(int position);
    }
    private OnUserClickListener listener;
    public void setOnClickListener(OnUserClickListener listener){
        this.listener = listener;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(view, listener);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
     User currentUser = users.get(position);
     holder.avatar.setImageResource(currentUser.GetAvatarResource());
     holder.UserName.setText(currentUser.GetName());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    public UserAdapter(ArrayList<User> users){
        this.users = users;
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder{
        public ImageView avatar;
        public TextView UserName;
        public UserViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            avatar = itemView.findViewById(R.id.Avatar);
            UserName = itemView.findViewById(R.id.UserNameTextView);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onUserClick(position);
                        }
                    }
                }
            });
        }
    }
}
