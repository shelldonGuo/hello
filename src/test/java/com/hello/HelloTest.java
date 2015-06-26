package com.hello;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by guoxuedong on 15-6-25.
 */
public class HelloTest {
    @Test
    public void test() throws IOException {
        String path = "/tmp/xx.log";
        FileWriter fileWriter = new FileWriter(path);
        String hello = "hello";
        String world = "world";
        fileWriter.write(hello);
        fileWriter.write("\n");
        fileWriter.write(world);
        fileWriter.flush();
        fileWriter.close();

        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
