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
import androidx.fragment.app.FragmentManager;

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

import javax.crypto.spec.IvParameterSpec;


public class MainActivity3 extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final ArrayList<User> users = new ArrayList<User>();
    private String master;
    private String pass;
    private String username;
    private String salt;
    public static IvParameterSpec iv;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://redbird-password-manger-default-rtdb.firebaseio.com/").getReference();
    static boolean refresh = false;
    private long mLastClickTime = 0;
    private long rLastClickTime = 0;
    private long oLastClickTime = 0;
    private String search;
    private HashMap<String, String> nameAddresses;
    private List<HashMap<String, String>> listItems;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity3_main);
        final Activity them = this;

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        username = username.replace(".", "-");
        master = intent.getStringExtra("masterPass");
        DatabaseReference userDb = mDatabase.child("users").child(username).child("storedPasswords");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Iterable<DataSnapshot> children = snapshot.getChildren();
                String website = null;
                String username = null;
                String password = null;
                String salt = null;



                    for (DataSnapshot it : children) {
                        DataSnapshot i = it;
                        try {
                            website = i.child("website").getValue().toString();
                        }catch(NullPointerException e) {
                            System.out.println("Website is null");
                            e.printStackTrace();
                        }
                        try {
                             username = i.child("userName").getValue().toString();
                        }catch(NullPointerException e){
                            System.out.println("username is null");
                            e.printStackTrace();
                        }
                        try {
                             password = i.child("password").getValue().toString();
                        }catch (NullPointerException e){
                            System.out.println("password is null");
                            e.printStackTrace();
                        }
                        try {
                            salt = i.child("salt").getValue().toString();
                        }catch (NullPointerException e){
                            System.out.println("salt is null");
                            e.printStackTrace();
                        }
                        try {

                        }catch (NullPointerException e){
                            System.out.println("iv is null");
                            e.printStackTrace();
                        }

                        User user = new User(website, username, password, salt);
//                        Log.d("user test", "is " + iv.toString());

                        users.add(user);
                    }
                    ListView resultsListView = findViewById(R.id.results_listview);
                    resultsListView.setOnItemClickListener((AdapterView.OnItemClickListener) them);


                    nameAddresses = new HashMap<>();


                    for (User i : users) {
                        nameAddresses.put(i.website, i.username);

                    }

                    listItems = new ArrayList<>();
                adapter = new SimpleAdapter(them, listItems, R.layout.list_item,
                        new String[]{"First Line", "Second Line"},
                        new int[]{R.id.text1, R.id.text2});

                    Iterator it = nameAddresses.entrySet().iterator();
                    while (it.hasNext()) {
                        HashMap<String, String> resultsMap = new HashMap<>();
                        Map.Entry pair = (Map.Entry) it.next();
                        try {
                            resultsMap.put("First Line", pair.getKey().toString());
                            resultsMap.put("Second Line", pair.getValue().toString());
                            listItems.add(resultsMap); //the list item values are added to listItems arrayList, call from it to get the values
                        }catch (NullPointerException e){
                            System.out.println("Hashmap tripped"); //return to this - app used to crash on Second line results map for some reason
                        }
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

    public boolean isNotNull(Iterable<DataSnapshot> children){
        for (DataSnapshot it : children) {
            DataSnapshot i = it;
            if(i.child("website").getValue().toString().equals(null)){
                System.out.println("Website is null");
                return false;
            }
            if(i.child("userName").getValue().toString().equals(null)){
                System.out.println("Website is null");

                return false;
            }
            if (i.child("password").getValue().toString().equals(null)) {
                System.out.println("Website is null");

                return false;
            }
        }
        return true;
    }

    public void search(String input) {
        ListView resultsListView = findViewById(R.id.results_listview);
        resultsListView.setOnItemClickListener((AdapterView.OnItemClickListener) this);
        int position = 0;
        boolean flag = false;
        List<HashMap<String, String>> orderedItems = new ArrayList<>();
        for (HashMap<String, String> i : listItems) {
            if (flag == false) {
                position++;
            }
            if (i.get("First Line").equalsIgnoreCase(input)) {
                orderedItems.add(i);
                flag = true;
            }
        }
        if (flag == true) {
            listItems.remove(position - 1);

            listItems.add(0, orderedItems.get(0));
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(MainActivity3.this, "No results found", Toast.LENGTH_LONG).show();

        }

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
                salt = u.salt;


                //Log.d("Testing", "Iv signature is " + iv.toString());

            }
        }
        intent.putExtra("masterPass", master);
        intent.putExtra("pass", pass);
        intent.putExtra("salt", salt);
        startActivity(intent);//Application will crash if it is uncommented
    }

    @Override
    //Used for the toolbar that I created,(I deleted the action bar to make it more easier to add icons)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        try {
            getSupportActionBar().setTitle("Redbird PM");
        } catch (NullPointerException e) {
        }


        return true;
    }

    @Override
    //Toast message that will display the following if clicked, optional ofcourse
    public boolean onOptionsItemSelected(MenuItem item) {

        String msg = "";
        switch (item.getItemId()) {

            case R.id.back:
                if (SystemClock.elapsedRealtime() - oLastClickTime < 1000) {
                    break;
                }
                oLastClickTime = SystemClock.elapsedRealtime();

                try {
                    msg = "Back";
                    submit();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.add:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    break;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                msg = "Add";

                create();
                break;
            case R.id.app_bar_search:
                msg = "Search";
                showAlertDialog();
                break;
        }
        Toast.makeText(MainActivity3.this, msg + "  Pressed", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    public void create() {
        Intent intent = new Intent(this, MainActivity4.class);
        intent.putExtra("account", users);
        intent.putExtra("username", username);
        intent.putExtra("pass", pass);
        intent.putExtra("masterPass", master);
        startActivity(intent);

    }

    public void showAlertDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SearchFragment alertDialog = SearchFragment.newInstance("Search");
        alertDialog.setCancelable(true);
        alertDialog.show(fm, "fragment_alert");
        alertDialog.setDialogResult(new SearchFragment.OnMyDialogResult() {
            public void finish(String result) {
                if (result.isEmpty() || result.equals(null)) {

                } else {
                    search(result);
                }
            }
        });
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
            if (SystemClock.elapsedRealtime() - rLastClickTime < 50000000) {
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





