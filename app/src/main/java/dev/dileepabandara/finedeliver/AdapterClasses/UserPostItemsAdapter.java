/*
   --------------------------------------
      Developed by
      Dileepa Bandara
      https://dileepabandara.github.io
      contact.dileepabandara@gmail.com
      ©dileepabandara.dev
      2021
   --------------------------------------
*/

package dev.dileepabandara.finedeliver.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import dev.dileepabandara.finedeliver.HelperClasses.UserPostItemHelperClass;
import dev.dileepabandara.finedeliver.R;

import java.util.ArrayList;

public class UserPostItemsAdapter extends RecyclerView.Adapter<UserPostItemsAdapter.ViewHolder> {

    Context context;
    ArrayList<UserPostItemHelperClass> userPostItemHelperClassList;

    public UserPostItemsAdapter(Context c, ArrayList<UserPostItemHelperClass> p) {

        this.userPostItemHelperClassList = p;
        this.context = c;

    }


    @NonNull
    @Override
    public UserPostItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_posted_items_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostItemsAdapter.ViewHolder holder, int position) {
        final UserPostItemHelperClass userPostItemHelperClass = userPostItemHelperClassList.get(position);
        Glide.with(context).load(userPostItemHelperClass.getItemImageUri()).into(holder.imgUserPostedItemImage);
        //Picasso.get().load(userPostItemHelperClass.getItemImageUri()).into(holder.imgUserPostedItemImage);
        holder.txtUserPostedItemOrderName.setText("Order Name: " + userPostItemHelperClass.getPostOrderName());
        holder.txtUserPostedItemOrderId.setText("Order ID: " + userPostItemHelperClass.getOrderID());
        holder.txtUserPostedItemDueDate.setText("Order Due Date: " + userPostItemHelperClass.getPostItemDueDatAndTime());

    }

    @Override
    public int getItemCount() {
        if (userPostItemHelperClassList != null) {
            return userPostItemHelperClassList.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUserPostedItemImage;
        TextView txtUserPostedItemOrderName, txtUserPostedItemOrderId, txtUserPostedItemDueDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUserPostedItemImage = itemView.findViewById(R.id.imgUserPostedItemImage);
            txtUserPostedItemOrderName = itemView.findViewById(R.id.txtUserPostedItemOrderName);
            txtUserPostedItemOrderId = itemView.findViewById(R.id.txtUserPostedItemOrderId);
            txtUserPostedItemDueDate = itemView.findViewById(R.id.txtUserPostedItemDueDate);

        }
    }

}
