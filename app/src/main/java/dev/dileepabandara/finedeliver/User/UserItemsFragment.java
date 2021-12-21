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

package dev.dileepabandara.finedeliver.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import dev.dileepabandara.finedeliver.AdapterClasses.UserPostItemsAdapter;
import dev.dileepabandara.finedeliver.HelperClasses.UserPostItemHelperClass;
import dev.dileepabandara.finedeliver.R;
import dev.dileepabandara.finedeliver.User.UserItems.UserPostItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserItemsFragment extends Fragment {

    Button btnPostItem;
    RecyclerView recyclerView;
    ArrayList<UserPostItemHelperClass> arrayList;
    UserPostItemsAdapter userPostItemsAdapter;
    DatabaseReference databaseReference, dataR;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_items, container, false);

        btnPostItem = v.findViewById(R.id.btnPostItem);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        recyclerView = v.findViewById(R.id.userPostedItemsRecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        arrayList = new ArrayList<>();

        swipeRefreshLayout.setColorSchemeResources(R.color.fd_red);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                /*//   Get User Details
                swipeRefreshLayout.setRefreshing(true);
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                dataR = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                dataR.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDetailsHelperClass userDetailsHelperClass = snapshot.getValue(UserDetailsHelperClass.class);
                        String userName = userDetailsHelperClass.getUserName();
                        String userPhone = userDetailsHelperClass.getUserPhone();
                        String userEmail = userDetailsHelperClass.getUserEmail();
                        postedItemsMenu();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/
                postedItemsMenu();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postedItemsMenu();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        btnPostItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UserPostItem.class);
                startActivity(i);
            }
        });

        return v;
    }


    private void postedItemsMenu() {

        try {
            //  Get User posted items
            arrayList.clear();
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Orders").child(userID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            UserPostItemHelperClass p = snapshot1.getValue(UserPostItemHelperClass.class);
                            arrayList.add(p);
                        }

                        userPostItemsAdapter = new UserPostItemsAdapter(getContext(), arrayList);
                        recyclerView.setAdapter(userPostItemsAdapter);
                        userPostItemsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "No orders available", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}