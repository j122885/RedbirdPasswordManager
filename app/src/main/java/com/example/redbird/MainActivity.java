package com.example.redbird;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;

//v1.0
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
    String personEmail;
    private long mLastClickTime = 0;
    private long rLastClickTime = 0;
    private static SecretKeySpec skeySpec;
    private static byte[] key;
    private static TextView et;
    static GoogleSignInClient mGoogleSignInClient;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private Cipher cipher;
    private String KEY_NAME = "AndroidKey";

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
        //error.setVisibility(View.INVISIBLE);
        et = findViewById(R.id.errorMessage);//this is a static version of error message
        passwordError = findViewById(R.id.passwordError);
        passwordError.setVisibility(View.INVISIBLE);
        //Internal File storage
        context = getApplicationContext();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

//        AsyncTask<Void, Void, String> response = new IPFSConfig("QmUg6o13CxZH4sBDKfyU5gQkNyurBmC74ESmtCd3ma1CDi", null, false, true).execute(); //start instance of IPFS
//        try {
//            System.out.println("Response from main activity: " + response.get()); //user response .get() to retrieve result from AsyncTask
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }




    }


    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
    public void fingerprint(View v){ //keep this here in case you want to reconfigure login for fingerprints in future
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if(!fingerprintManager.isHardwareDetected()){

                error.setText("Fingerprint Scanner not detected in Device");

            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){

                error.setText("Permission not granted to use Fingerprint Scanner");

            } else if (!keyguardManager.isKeyguardSecure()){

                error.setText("Add Lock to your Phone in Settings");

            } else if (!fingerprintManager.hasEnrolledFingerprints()){

                error.setText("You should add atleast 1 Fingerprint to use this Feature");

            } else {

                error.setText("Place your Finger on Scanner to Access the App.");

                generateKey();

                if (cipherInit()){

                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler fingerprintHandler = new FingerprintHandler(this);
                    fingerprintHandler.startAuth(fingerprintManager, cryptoObject);

                }
            }

        }
    }

    private void updateUI(FirebaseUser currentUser) {
        FirebaseUser acct = currentUser;
        if (acct != null) {
            personEmail = acct.getEmail();
            SignIn test = new SignIn(personEmail);
            System.out.println(acct.getEmail());
            //Go to next transition Activity
            Intent intent = new Intent(this, MasterLoginActivity.class);
            intent.putExtra("username", personEmail.toLowerCase());
            startActivity(intent);
        }
    }
    private void updateUIRegister(FirebaseUser currentUser) {
        FirebaseUser acct = currentUser;
        if (acct != null) {
            personEmail = acct.getEmail();
            SignIn test = new SignIn(personEmail);
            System.out.println(acct.getEmail());
            //Go to next transition Activity
            Intent intent = new Intent(this, MasterActivity.class);
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

                            boolean newuser = task.getResult().getAdditionalUserInfo().isNewUser();



                            if(newuser){
                                updateUIRegister(user);
                                //Do Stuffs for new user

                            }else{
                                    updateUI(user);
                                //Continue with Sign up
                            }
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
        Context context = getApplicationContext();
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


    public void register(View view) throws Exception { // New Firebase user creation
        passwordError.setVisibility(View.INVISIBLE);
        error.setVisibility(View.INVISIBLE);//sets warning to invisible
        String username = rUser.getText().toString();//gets username from Edit text
        String password = rPass.getText().toString();//gets passworkd from Edit text
        if (!username.isEmpty()) {
            if (validEmail(username)) {
                if (minimumPassword(password)) {

                    mAuth.createUserWithEmailAndPassword(username, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        updateUIRegister(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Context context = getApplicationContext();

                                        Toast.makeText(context, "The email address is already in use by another account.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                } else {
                    passwordError.setVisibility(View.VISIBLE);
                }
            } else {
                Context context = getApplicationContext();
                Toast.makeText(context, "Invalid Email format",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            System.out.println("Username field is empty");

        }
    }

    public void login(View view) throws Exception {

        passwordError.setVisibility(View.INVISIBLE);
        error.setVisibility(View.INVISIBLE);//sets warning to invisible
        String password = rPass.getText().toString();//gets passworkd from Edit text
        String username = rUser.getText().toString();//gets username from Edit text

        if (password.isEmpty() || username.isEmpty()) {

        } else {
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user.isEmailVerified()){
                                    updateUI(user);
                               }else{
                                    Toast.makeText(context, "Please validate your email address.",
                                        Toast.LENGTH_SHORT).show();
                               }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Context context = getApplicationContext();

                                Toast.makeText(context, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
        }

    }

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

    public boolean validEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public void resetPassword(View view) {
        Intent intent = new Intent(this, PasswordResetActivity.class);
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

        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() {

        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

        } catch (KeyStoreException | IOException | CertificateException
                | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | NoSuchProviderException e) {

            e.printStackTrace();

        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }


        try {

            keyStore.load(null);

            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            return true;

        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }

    }

}


















