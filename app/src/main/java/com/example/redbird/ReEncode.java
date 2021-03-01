package com.example.redbird;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public interface ReEncode {


    //@RequiresApi(api = Build.VERSION_CODES.N)
//    public default submit(String pass, File encodeFrom , File encodeTo ) throws Exception {
//
//        //Encodes from newfile
//        byte [] fileData = MainActivity.fileToByteArray(encodeFrom);
//        MainActivity.generateKey(pass);
//        byte [] encodedData = MainActivity.encodeFile(fileData);
//
//        //empties newFile
//        try {
//            FileWriter writer = new FileWriter(encodeFrom, false);
//            writer.flush();
//            writer.close();
//            System.out.println("Successfully erased the file contents.");
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
//
//        //puts the file bytes into transfile - holds encoded data
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(encodeTo));
//        bos.write(encodedData);//write it to transFile
//        bos.flush();
//        bos.close();
//
//    }












}
