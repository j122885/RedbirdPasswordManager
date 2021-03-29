package com.example.redbird;
import android.os.AsyncTask;

import java.io.IOException;

import javax.annotation.Nullable;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

public class IPFSConfig extends AsyncTask<Void, Void, String> {
    private String hash; //hash from another file
    private String pass;
    private IPFS ipfs;
    private boolean write;
    private boolean read;
    private String hashedPw;
    public IPFSConfig(@Nullable String hash, @Nullable String pass, boolean write, boolean read) {
        this.hash = hash;
        this.pass = pass;
        this.write = write;
        this.read = read;
    }


    @Override
    protected String doInBackground(Void... voids) {
        ipfs = new IPFS("/dnsaddr/ipfs.infura.io/tcp/5001/https");

        try {
            System.out.println("connected");

            System.out.println("id: " + ipfs.stats.toString());
        } catch (Exception e) {
            System.out.println("not connected" + e);
        }
        if (write) {
          return  hashedPw = writeToIPFS();

        }
        if (read) {
          return hashedPw =  readFromIPFS();
        }

        return null;
    }

    private String writeToIPFS() { // add a string parameter (pass) for password of just call the variable
        try {
            NamedStreamable.ByteArrayWrapper bytearray = new NamedStreamable.ByteArrayWrapper(pass.getBytes());   //write to ipfs
            MerkleNode response = ipfs.add(bytearray).get(0);
            System.out.println("Hash (base 58): " + response.hash.toBase58()); // response is a hash
            return response.hash.toBase58();
        } catch (IOException ex) {
            throw new RuntimeException("Error whilst communicating with the IPFS node", ex);
        }

    }

    private String readFromIPFS() {
        try {
            //  String hash = "QmWfVY9y3xjsixTgbd9AorQxH7VtMpzfx2HaWtsoUYecaX"; // Hash of a file                 //read from ipfs
            Multihash multihash = Multihash.fromBase58(hash);
            byte[] content = ipfs.cat(multihash);
            System.out.println("Content of " + hash + ": " + new String(content));
            return new String(content);
        } catch (IOException ex) {
            throw new RuntimeException("Error whilst communicating with the IPFS node", ex);
        }
    }

    @Override
    protected void onPostExecute(String result){ //takes result from doInBackground and puts it in .get()

    }



}

