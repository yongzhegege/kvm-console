package com.example.demo.util;

import java.util.Random;

public class MacUtil {
	public static String getMacAddr(){
		Random random = new Random();
        String[] mac = {
                String.format("%02x", 0x32),
                String.format("%02x", 0x54),
                String.format("%02x", 0x03),
                String.format("%02x", random.nextInt(0xff)),
                String.format("%02x", random.nextInt(0xff)),
                String.format("%02x", random.nextInt(0xff))
        };
        String addr="";
        int i=0;
        for(String s:mac){
        	if(i==0){
        		addr=addr+s;
        	}else{
        		addr=addr+":"+s;
        	}
        	i++;
        }
        return addr;
	}
}
