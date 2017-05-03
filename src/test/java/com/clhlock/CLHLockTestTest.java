package com.clhlock;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by guoxuedong on 15-6-25.
 */
public class CLHLockTestTest {
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
        String line = bufferedReader.readLine();
        Assert.assertEquals(hello, line);

        line = bufferedReader.readLine();
        Assert.assertEquals(world, line);
    }
}
