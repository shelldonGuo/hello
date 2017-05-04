package remove.comments;

import java.io.File;
/**
 * Created by guoxuedong on 17-5-3.
 * code with some comments
 * in format // and /*
 *
 */
public class RemoveCommentsCode {

    public static void main(String[] args) throws IOException {
        // print some lines
        System.out.println("1. // hello "); // first line
        System.out.println("2. /* world */ "); // second line
        System.out.println("3. \"// hello \" \"/* world */\""); /* third line */

        char ch1 = '"';
        char ch2 = '\n';
        char ch3 = '\"';
        char ch4 = '\'';
        char ch5 = '\\';

        /* exit */
        System.exit(0);
    }
}
