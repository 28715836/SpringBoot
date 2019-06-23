package com.example.springboot.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PythonTest {

    public static void main(String[] args) {
        try {
            System.out.println("start");
            String[] args1 = new String[]{"python", "C:\\Users\\DELL\\Desktop\\manage\\task_bd_commission\\task\\test\\GenerateTest.py"};
            Process pr = Runtime.getRuntime().exec(args1);

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            pr.waitFor();
            System.out.println("end");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
