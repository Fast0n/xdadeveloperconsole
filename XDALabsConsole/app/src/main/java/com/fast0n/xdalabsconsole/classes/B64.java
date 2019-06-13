package com.fast0n.xdalabsconsole.classes;

import android.util.Base64;

public class B64 {

    public byte[] encodeString(String plainText){
        return Base64.encode(plainText.getBytes(), Base64.DEFAULT);
    }

    public byte[] decodeString(String plainText){
        return Base64.decode(plainText.getBytes(), Base64.DEFAULT);
    }

    public byte[] encodeBinary(byte[] binary){
        return Base64.encode(binary, Base64.DEFAULT);
    }

    public byte[] decodeBinary(byte[] binary){
        return Base64.decode(binary, Base64.DEFAULT);
    }
}
