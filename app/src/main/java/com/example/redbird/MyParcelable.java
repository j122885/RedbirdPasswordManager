package com.example.redbird;

import android.os.Parcel;
import android.os.Parcelable;

import javax.crypto.spec.IvParameterSpec;

public class MyParcelable implements Parcelable {

    private IvParameterSpec iv;
    private byte[] bytes;
    private int mData;

    public MyParcelable(IvParameterSpec iv) {
        this.iv = iv;
        System.out.println("parcel " + this.iv.toString());
    }



    public static final Creator<MyParcelable> CREATOR = new Creator<MyParcelable>() {
        @Override
        public MyParcelable createFromParcel(Parcel in) {
            return new MyParcelable(in);
        }

        @Override
        public MyParcelable[] newArray(int size) {
            return new MyParcelable[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeByteArray(iv.getIV());

    }
    protected MyParcelable(Parcel in) {
        in.readByteArray(bytes);
    }
    @Override
    public int describeContents() {
        return 0;
    }

}
