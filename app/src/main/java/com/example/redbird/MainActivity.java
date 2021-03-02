package com.example.redbird;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    EditText rUser;
    EditText rPass;
    TextView error;
    TextView passwordError;
    ToggleButton registerSwitch;
    ToggleButton loginSwitch;
    Button registerButton;
    Button loginButton;
    Context context;
    File directory;
    File accountRepo;
    File user;
    String personEmail;
    String personId;
    private long mLastClickTime = 0;
    private long rLastClickTime = 0;
    private static SecretKeySpec skeySpec;
    private static byte[] key;
    private static TextView et;
    static GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rUser = findViewById(R.id.rUser);
        rPass = findViewById(R.id.rPass);
        registerSwitch = (ToggleButton) findViewById(R.id.registerSwitch);
        registerSwitch.setBackgroundColor(Color.parseColor("#FF1E1C1C"));//initially sets background to gray or enabled
        loginSwitch = (ToggleButton) findViewById(R.id.loginSwitch);
        loginSwitch.setBackgroundColor(Color.parseColor("#0C0C0C"));//initially sets background to black or disabled
        registerButton = (Button) findViewById(R.id.registerButton);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setVisibility(View.INVISIBLE);
        error = findViewById(R.id.errorMessage);
        error.setVisibility(View.INVISIBLE);
        et = findViewById(R.id.errorMessage);//this is a static version of error message
        passwordError = findViewById(R.id.passwordError);
        passwordError.setVisibility(View.INVISIBLE);
        //Internal File storage
        context = getApplicationContext();
        directory = context.getFilesDir();//the root directory (files)
        System.out.println(directory); ///data/user/0/com.example.redbirdpasswordmanager/files
        accountRepo = new File(directory, "Account");//Create Account directory
        if (!accountRepo.exists()) { //checks if it exits or not
            if (accountRepo.mkdir()) { //creates it if it does not

            }
        }
        System.out.println(context.getDir("Account", 0));//Test to see if Account dir was created

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    private void updateUI(FirebaseUser currentUser) {
        FirebaseUser acct = currentUser;
        if (acct != null) {
            personEmail = acct.getEmail();
            SignIn test = new SignIn(personEmail);
            System.out.println(acct.getEmail());
            //Go to next transition Activity
            Intent intent = new Intent(this, TransitionActivity.class);
            intent.putExtra("username", personEmail.toLowerCase());
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//
//            // Signed in successfully, show authenticated UI.
//            System.out.println(account.getEmail());
//            updateUI(account);
//
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
//           // updateUI(null);
//        }
//    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            System.out.println(user.getEmail());
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            //   Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //  updateUI(null);
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(GoogleSignInAccount account) {//come back to this it keeps skipping the signin
        GoogleSignInAccount acct = account;
        if (acct != null) {
            personEmail = acct.getEmail();
            personEmail = personEmail.replace(".", "-");

            SignIn test = new SignIn(personEmail);

            //Go to next transition Activity
            Intent intent = new Intent(this, TransitionActivity.class);
            intent.putExtra("username", personEmail.toLowerCase().replace(".", "-"));
            startActivity(intent);
        }
    }

    public void changeToLogin(View view) {
        registerSwitch.setBackgroundColor(Color.parseColor("#0C0C0C"));
        registerButton.setVisibility(view.INVISIBLE);
        loginButton.setVisibility(view.VISIBLE);
        loginSwitch.setBackgroundColor(Color.parseColor("#FF1E1C1C"));

    }


    public void changeToRegister(View view) {
        registerSwitch.setBackgroundColor(Color.parseColor("#FF1E1C1C"));
        registerButton.setVisibility(view.VISIBLE);
        loginButton.setVisibility(view.INVISIBLE);
        loginSwitch.setBackgroundColor(Color.parseColor("#0C0C0C"));

    }


    //    public void register(View view) throws Exception {
//        if (SystemClock.elapsedRealtime() - rLastClickTime < 10000) {
//            return;
//        }
//        rLastClickTime = SystemClock.elapsedRealtime();
//
//        passwordError.setVisibility(View.INVISIBLE);
//        error.setVisibility(View.INVISIBLE);//sets warning to invisible
//        String username = rUser.getText().toString();//gets username from Edit text
//        //boolean nonexistent = false;
//        File pass = null;
//        user = new File(accountRepo, username.toLowerCase());//create an internal directory with name of username
//
//
//        if (!user.exists() && minimumPassword(rPass.getText().toString())) { //checks if it exits or not and checks if the password is at minimum standards
//            user.mkdirs();
//            generateKey(rPass.getText().toString());//generate key
//            Log.d("User File", String.valueOf((context.getDir(username.toLowerCase(), 0))));//checks if file was  made
//
//            pass = new File(context.getDir(username.toLowerCase(), 0), "Passwords.txt");//creates a text file inside username folder and holds passwords
//            if (!pass.exists()) {
//                pass.createNewFile();//if it doesn't exit make it
//            }
//            //write to file just to put some data in it
//            try {
//                FileWriter myWriter = new FileWriter(pass);
//                myWriter.write("apple tttripple apple ");
//                myWriter.close();
//                System.out.println("Successfully wrote to the file.");
//            } catch (IOException e) {
//                System.out.println("An error occurred.");
//                e.printStackTrace();
//            }
//
//            File transFile = new File(context.getDir(username.toLowerCase(), 0), "trans.txt");//holds the encrypted file data from password
//            if (!transFile.exists()) {
//                transFile.createNewFile();
//            }
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(transFile));
//            byte[] toByteArray = fileToByteArray(pass);//turn file data to bytes
//
//            byte[] filesBytes = encodeFile(toByteArray);//encrypt byte array
//            bos.write(filesBytes);//write it to transFile
//            bos.flush();
//            bos.close();
//
//            //writeto file just to flush the data from password
//            try {
//                FileWriter myWriter = new FileWriter(pass, false);
//                myWriter.flush();
//                myWriter.close();
//                System.out.println("Successfully wrote to the file.");
//            } catch (IOException e) {
//                System.out.println("An error occurred.");
//                e.printStackTrace();
//            }
//
//
//            File newFile = new File(context.getDir(username.toLowerCase(), 0), "holder.txt");//holds decrypted data
//            if (!newFile.exists()) {
//                newFile.createNewFile();
//            }
//
//            Log.d("Parent of pass file", String.valueOf(user.getParentFile())); ///data/user/0/com.example.redbirdpasswordmanager/files/Account/sfas
//
//
//            File[] files = context.getDir(username.toLowerCase(), 0).listFiles(); //says if there are files
//            if (files.length == 0) {
//                System.out.println("This has no files");
//            } else {
//                System.out.println(files.length);
//                System.out.println("There are files:");
//            }
//
//            login(view);
//
//        } else if (user.exists()) {
//            error.setVisibility(View.VISIBLE);
//            error.setText("Username already exists");
//        }
//        if (!minimumPassword(rPass.getText().toString())) {
//            passwordError.setVisibility(View.VISIBLE);
//            passwordError.setText("Password does not follow criteria");
//        }
//    }
    public void register(View view) throws Exception { // New Firebase user creation
        passwordError.setVisibility(View.INVISIBLE);
        error.setVisibility(View.INVISIBLE);//sets warning to invisible
        String username = rUser.getText().toString();//gets username from Edit text
        String password = rPass.getText().toString();//gets passworkd from Edit text
        if (!username.isEmpty()) {
            System.out.println("Username field is empty");
            if (minimumPassword(password)) {

                mAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                                    //  updateUI(null);
                                }


                            }
                        });
            } else {
                passwordError.setVisibility(View.VISIBLE);
            }
        }
    }

    public void login(View view) throws Exception {

        passwordError.setVisibility(View.INVISIBLE);
        error.setVisibility(View.INVISIBLE);//sets warning to invisible
        String password = rPass.getText().toString();//gets passworkd from Edit text
        String username = rUser.getText().toString();//gets username from Edit text


        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            // ...
                        }

                        // ...
                    }
                });

    }
//    static byte[] getByte(String path) {
//        byte[] getBytes = {};
//        try {
//            File file = new File(path);
//            getBytes = new byte[(int) file.length()];
//            InputStream is = new FileInputStream(file);
//            is.read(getBytes);
//            is.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return getBytes;
//    }


//    public static void byteToFile(byte[] b, File destination) {
//
//        try {
//            FileOutputStream fos = new FileOutputStream(destination);
//
//            fos.write(b);
//            fos.close();
//        } catch (FileNotFoundException ex) {
//            System.out.println("FileNotFoundException : " + ex);
//        } catch (IOException ioe) {
//            System.out.println("IOException : " + ioe);
//        }
//
//
//    }


    public boolean minimumPassword(String pass) {
        ArrayList<Character> chars = new ArrayList<Character>();
        Boolean capital = false;
        Boolean lowercase = false;
        Boolean number = false;
        Boolean notEnoughChars = false;
        if (pass.length() > 7) {
            for (int i = 0; i < pass.length(); i++) {
                if (Character.isUpperCase(pass.charAt(i))) {
                    capital = true;
                }
                if (Character.isLowerCase(pass.charAt(i))) {
                    lowercase = true;
                }
                if (Character.isDigit(pass.charAt(i))) {
                    number = true;
                }
            }
            if (capital && lowercase && number) {
                System.out.println("Password verification passed");
                return true;
            }
        }
        System.out.println("Password verification failed");

        return false;

    }


//    public boolean passswordCheck(String username, String password) throws Exception { //checks if the password for the account is correct
//
//        generateKey(password);
//        File[] files = context.getDir(username.toLowerCase(), 0).listFiles(); //says if there are files
//        if (files.length == 0) {
//            System.out.println("This has no files");
//        } else {
//            System.out.println(files.length);
//            System.out.println("There are files:");
//
//        }
//
//
//        byte[] transEncoded = getByte(String.valueOf(files[1]));
//        try {
//            byte[] decodedData = decodeFile(transEncoded);
//            return true;
//        } catch (IllegalBlockSizeException e) {
//            return false;
//        } catch (BadPaddingException e) {
//            return false;
//        } catch (NullPointerException e) {
//            return false;
//        }
//    }


//    public void login(View view) throws Exception {
//        System.out.println("once");
//        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//            return;
//        }
//        mLastClickTime = SystemClock.elapsedRealtime();
//
//        error.setVisibility(View.INVISIBLE);//sets warning to invisible
//        System.out.println("starting login");
//        File accounts = accountRepo;
//        System.out.println(accountRepo.listFiles().length);
//        File thisAccount;
//        File[] accountList = accounts.listFiles();//contains the files in accountRepo
//        System.out.println(rUser.getText());
//        boolean found = false;
//        String username = rUser.getText().toString();
//        for (File i : accountList) {
//            //System.out.println(i);
//            if (i.getName().equals(username.toLowerCase())) {//gets the name and checks to see if it is equal to the username input
//                thisAccount = i;//stores the file if found inside thisAccount
//                found = true;
//                System.out.println("Found the file");
//
//                File[] outFiles = i.listFiles();
//
//                if (passswordCheck(i.getName(), rPass.getText().toString()) == true) {
//
//                    //Decode trans.txt
//                    File[] files = context.getDir(username.toLowerCase(), 0).listFiles();
//                    try {
//                        byte[] transEncoded = getByte(String.valueOf(files[1]));
//                        byte[] decodedData = decodeFile(transEncoded);//decode the file byte array part that
//                        byteToFile(decodedData, files[2]); //store the decoded data in a new file
//
//                    } catch (NullPointerException e) {
//                        System.out.println("Not correct password \n");
//                    }
//
//                    //Go to next transition Activity
//                    Intent intent = new Intent(this, TransitionActivity.class);
//                    intent.putExtra("username", username.toLowerCase());
//                    intent.putExtra("pass", rPass.getText().toString());
//                    startActivity(intent);
//                } else {
//                    System.out.println("Password not correct");
//                    error.setText("Password not correct");
//                    error.setVisibility(View.VISIBLE);
//                }
//            }
//        }
//        if (!found) {
//            System.out.println("File not found");
//            error.setText("Account not found. Please try again.");
//            error.setVisibility(View.VISIBLE);
//        }
//
//    }


//    public static byte[] fileToByteArray(File file) throws FileNotFoundException, IOException {
//        byte[] bytes = new byte[(int) file.length()];
//        int size = (int) file.length();
//        try {
//            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
//            buf.read(bytes, 0, bytes.length);
//            buf.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bytes;
//    }
//
//
//    public static void generateKey(String password) throws Exception {
//        MessageDigest sha = null;
//        try {
//            key = password.getBytes("UTF-8");
//            sha = MessageDigest.getInstance("SHA-1");
//            key = sha.digest(key);
//            key = Arrays.copyOf(key, 16);
//            skeySpec = new SecretKeySpec(key, "AES");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static byte[] encodeFile(byte[] fileData) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//
//        byte[] inputBytes = cipher.doFinal(fileData);
//        return inputBytes;
//    }
//
//    public static byte[] decodeFile(byte[] fileData) throws Exception {
//        // try{
//
//
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//        byte[] outputBytes = cipher.doFinal(fileData);
//
//
//        return outputBytes;
//
//
//    }

    //    public void register(View view) throws Exception {
//        if (SystemClock.elapsedRealtime() - rLastClickTime < 10000) {
//            return;
//        }
//        rLastClickTime = SystemClock.elapsedRealtime();
//
//        passwordError.setVisibility(View.INVISIBLE);
//        error.setVisibility(View.INVISIBLE);//sets warning to invisible
//        String username = rUser.getText().toString();//gets username from Edit text
//
//
//    }
    private boolean shouldAllowBack() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

        }
    }
}