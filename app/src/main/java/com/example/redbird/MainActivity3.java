package com.example.redbird;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity3 extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ArrayList<User> users = new ArrayList<User>();


    String pass;

    String username;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://redbird-password-manger-default-rtdb.firebaseio.com/").getReference();
    static boolean refresh = false;
    private long mLastClickTime = 0;
    private long rLastClickTime = 0;
    private long oLastClickTime = 0;
    HashMap<String, String> nameAddresses;
    List<HashMap<String, String>> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity3_main);
        final Activity them = this;

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        username = username.replace(".", "-");
        DatabaseReference userDb = mDatabase.child("users").child(username).child("storedPasswords");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Iterable<DataSnapshot> children = snapshot.getChildren();


                for (DataSnapshot it : children) {
                    DataSnapshot i = it;
                    String website = i.child("website").getValue().toString();
                    String username = i.child("userName").getValue().toString();
                    String password = i.child("password").getValue().toString();
                    User user = new User(website, username, password);
                    users.add(user);
                }
                ListView resultsListView = findViewById(R.id.results_listview);
                resultsListView.setOnItemClickListener((AdapterView.OnItemClickListener) them);
                //Toolbar toolbar = findViewById(R.id.toolbar);
                //setSupportActionBar(toolbar);

                //Lines 44-68 are used as an example so i could see how the layout is , optional)
                nameAddresses = new HashMap<>();

                // nameAddresses.put("www.Amazon.com", "pass123");

                for (User i : users) {
                    nameAddresses.put(i.website, i.username);//correct this?

                }

                listItems = new ArrayList<>();
                SimpleAdapter adapter = new SimpleAdapter(them, listItems, R.layout.list_item,
                        new String[]{"First Line", "Second Line"},
                        new int[]{R.id.text1, R.id.text2});

                Iterator it = nameAddresses.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap<String, String> resultsMap = new HashMap<>();
                    Map.Entry pair = (Map.Entry) it.next();
                    resultsMap.put("First Line", pair.getKey().toString());
                    resultsMap.put("Second Line", pair.getValue().toString());
                    listItems.add(resultsMap); //the list item values are added to listItems arrayList, call from it to get the values

                }
                resultsListView.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userDb.addChildEventListener(childEventListener);


    }


    //Intent for the listview(makes any listitem "clickable"
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 100) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();


        System.out.println("Is this working");
        System.out.println(listItems.get(0).get("First Line"));
        // Then you start a new Activity via Intent
        Intent intent = new Intent(this, MainActivity2.class);

        intent.putExtra("website", listItems.get(position).get("First Line"));
        intent.putExtra("user", listItems.get(position).get("Second Line")); //listime username
        intent.putExtra("username", username); //account user name
        for (User u : users) {
            if (u.website.equals(listItems.get(position).get("First Line"))) {
                pass = u.uPass;
            }
        }
        intent.putExtra("pass", pass);
        //Intent to send the list to another activity
        intent.putExtra("account", users);
        startActivity(intent);//Application will crash if it is uncommented
    }

    @Override
    //Used for the toolbar that I created,(I deleted the action bar to make it more easier to add icons)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    //Toast message that will display the following if clicked, optional ofcourse
    public boolean onOptionsItemSelected(MenuItem item) {
        String msg = "";
        switch (item.getItemId()) {

            case R.id.back:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 100) {
                    break;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                try {
                    //TransitionActivity.setAlreadyWentToPasswords(true);
                    submit();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.add:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 100) {
                    break;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                msg = "Add";

                create();
                break;

        }
        Toast.makeText(MainActivity3.this, msg + "  Checked", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    public void create() {
        Intent intent = new Intent(this, MainActivity4.class);
        intent.putExtra("account", users);
        intent.putExtra("username", username);
        intent.putExtra("pass", pass);
        startActivity(intent);

    }


    public void submit() throws Exception {


        Intent intent = new Intent(this, TransitionActivity.class);
        intent.putExtra("username", username);
        // intent.putExtra("pass", pass);
        startActivity(intent);
    }

    private boolean shouldAllowBack() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {
            if (SystemClock.elapsedRealtime() - rLastClickTime < .01) {
                return;
            }
            rLastClickTime = SystemClock.elapsedRealtime();

            try {
                // TransitionActivity.setAlreadyWentToPasswords(true);
                submit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}





