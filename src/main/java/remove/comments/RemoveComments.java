package remove.comments;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by guoxuedong on 17-5-3.
 * remove comments in file
 */
public class RemoveComments {

    public static void main(String[] args) throws IOException {

        RemoveComments rc = new RemoveComments();
        String data = FileUtils.readFileToString(new File(RemoveComments.class.getResource("/RemoveCommentsCode.java").getPath()), Charset.defaultCharset());
        String result = rc.removeComments(data);
        System.out.println(result);
    }

    public String removeComments(String curr) {
        StringBuilder builder = new StringBuilder();
        boolean isQuote = false;
        for (int i = 0; i < curr.length(); ++i) {
            if (!isQuote) {
                //now meet the close quote
                if (curr.charAt(i) == '/' && i + 1 < curr.length() && curr.charAt(i + 1) == '/') {
                    while (i < curr.length() && curr.charAt(i) != '\n')
                        i++;
                    continue;
                } else if (curr.charAt(i) == '/' && i + 1 < curr.length() && curr.charAt(i + 1) == '*') {
                    i += 2;
                    while (i < curr.length() && curr.charAt(i) != '*' && i + 1 < curr.length() && curr.charAt(i + 1) != '/')
                        i++;
                    i++;
                    continue;
                } else if (curr.charAt(i) == '"') {
                    builder.append(curr.charAt(i));
                    isQuote = true;
                    continue;
                }
                builder.append(curr.charAt(i));
            } else {
                while (i < curr.length() && curr.charAt(i) != '"')
                    builder.append(curr.charAt(i++));
                builder.append('"');
                isQuote = false;
            }
        }
        return builder.toString();
    }

    public String readFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("The file " + fileName + " not found!");
        }
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuilder sb = new StringBuilder();
        String tmp;
        try {
            while ((tmp = bf.readLine()) != null) {
                sb.append(tmp);
                sb.append("\n");
            }
            /* comment line1
            * comment line2
            */
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }

}
