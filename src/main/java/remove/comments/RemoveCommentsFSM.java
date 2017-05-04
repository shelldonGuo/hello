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
        File input = new File(RemoveCommentsFSM.class.getResource("/RemoveCommentsCode.java").getPath());

        RemoveCommentsFSM rc = new RemoveCommentsFSM();
        rc.initFSM();
        rc.process(input);
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

    public void process(File in) throws Exception {

        int c;
        String temp = "";

        int state = 0;

        BufferedReader br = new BufferedReader(new FileReader(in));
        while ((c = br.read()) != -1) {
            state = fsm[state][c];
            temp += (char) c;

            switch (state) {
                case 0:
                    System.out.print(temp);
                    temp = "";

                    break;
                case 9:
                    //注释结束态，输出注释中的换行
                    char[] cbuf = temp.toCharArray();
                    for (char ch : cbuf) {
                        if (ch == '\n') {
                            System.out.println();
                        }
                    }

                    temp = "";
                    break;
            }
        }
        br.close();
    }
}
