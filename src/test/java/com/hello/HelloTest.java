package com.hello;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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
        OutputStream outputStream = new FileOutputStream(path);
        String hello = "hello";
        String world = "world";
        outputStream.write(hello.getBytes());
        outputStream.write("\n".getBytes());
        outputStream.write(world.getBytes());
        outputStream.flush();
        outputStream.close();

        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
