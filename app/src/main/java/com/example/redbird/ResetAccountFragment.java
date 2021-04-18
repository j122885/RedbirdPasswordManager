package com.example.redbird;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ResetAccountFragment extends DialogFragment {

    EditText confirm;
    Button cancelButton;
    Button deleteButton;
    public ResetAccountFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ResetAccountFragment newInstance(String title) {
        ResetAccountFragment frag = new ResetAccountFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.resetaccount, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        cancelButton = view.findViewById(R.id.cancelButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        confirm = view.findViewById(R.id.confirmField);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        cancel();
        reset();

    }
    public void cancel(){
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

    }

    public void reset(){

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirm.getText().toString().equalsIgnoreCase("reset")) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://redbird-password-manger-default-rtdb.firebaseio.com/").getReference();
                    DatabaseReference userDb; //realtime databasse
                    DatabaseReference userDb2;//cloud

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    userDb = mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("storedPasswords");
                    userDb2 = mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("login");
                    userDb.removeValue();
                    userDb2.removeValue();

                    FirebaseStorage storage;
                    storage = FirebaseStorage.getInstance();
                    StorageReference storageRef;
                    storageRef = storage.getReference();
                    StorageReference ivRef = storageRef.child( user.getEmail().replace(".", "-"));

                    ivRef.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ListResult> task) {
                           List<StorageReference> paths  = task.getResult().getPrefixes();

                           for(int i = 0; i < paths.size();  i++ ){
                               if(!(i == paths.size()-1)) {

                                   paths.get(i).child("iv").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           // File deleted successfully
                                           Log.d("Files", " deleted successfully");

                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception exception) {
                                           Log.d("Files", " did not delete");
                                       }
                                   });
                               }else{

                                   paths.get(i).child("iv").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           // File deleted successfully
                                           Log.d("Files", " deleted successfully" );
                                           Intent intent = new Intent(getActivity(), MasterActivity.class);
                                           Context context = v.getContext();
                                           Toast.makeText(context, "User account has been reset.",
                                                   Toast.LENGTH_SHORT).show();

                                           intent.putExtra("username", user.getEmail());
                                           startActivity(intent);
                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception exception) {
                                           Log.d("Files", " did not delete" );
                                       }
                                   });
                               }
                           }

                        }
                    });
                    
                }
            }
        });
    }

}























