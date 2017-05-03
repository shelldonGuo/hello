package remove.comments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by guoxuedong on 2017/5/3.
 */
public class RemoveCommentsFSM {

    int[][] fsm = new int[10][128];


    public static void main(String[] args) throws Exception {
        RemoveCommentsFSM rcfsm = new RemoveCommentsFSM();
        rcfsm.initFSM();

        File input = new File(RemoveComments.class.getResource("/RemoveCommentsCode.java").getPath());
        rcfsm.handleFile(input);

    }

    public void initFSM() {
        //初始化状态机
        for (int i = 0; i < 128; i++) {
            fsm[0][i] = 0;
            fsm[1][i] = 0;
            fsm[2][i] = 2;
            fsm[3][i] = 3;
            fsm[4][i] = 3;
            fsm[5][i] = 5;
            fsm[6][i] = 5;
            fsm[7][i] = 7;
            fsm[8][i] = 7;
            fsm[9][i] = 0;
        }
        fsm[0]['/'] = 1;
        fsm[0]['"'] = 5;
        fsm[0]['\''] = 7;
        fsm[1]['/'] = 2;
        fsm[1]['*'] = 3;
        fsm[1]['"'] = 5;
        fsm[2]['\n'] = 9;
        fsm[3]['*'] = 4;
        fsm[4]['/'] = 9;
        fsm[4]['*'] = 4;
        fsm[5]['"'] = 0;
        fsm[5]['\\'] = 6;
        fsm[7]['\\'] = 8;
        fsm[7]['\''] = 0;
        fsm[9]['/'] = 1;
        fsm[9]['"'] = 5;
        fsm[9]['\''] = 7;

    }

    public char parseInt(int a) {
        char result = (char) a;
        return result;
    }

    public void handleFile(File in) throws Exception {

        int c;
        String temp = "";

        int state = 0;

        BufferedReader br = new BufferedReader(new FileReader(in));
        while ((c = br.read()) != -1) {

            state = fsm[state][c];
            temp = temp + parseInt(c);
            switch (state) {
                case 0:
                    System.out.print(temp);
                    temp = "";
                    break;
                case 9:
                    //注释结束态，将除了换行的字符全部替换为“ ”输出。
                    char[] cbuf = temp.toCharArray();
                    for (int i = 0; i < cbuf.length; i++) {
                        if (cbuf[i] != '\n') {
                            cbuf[i] = ' ';
                        }
                    }

                    System.out.print(cbuf);
                    temp = "";
                    break;
            }
        }
        br.close();
    }
}
