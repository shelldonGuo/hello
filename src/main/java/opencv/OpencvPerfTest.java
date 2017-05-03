package opencv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

//import com.common.perfcounter.PerfCounter;
import org.apache.commons.lang3.tuple.Pair;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;

public class OpencvPerfTest {
    static AtomicLong calcCost = new AtomicLong(0);

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static final MatOfInt thumbQualityParams = new MatOfInt(Imgcodecs.CV_IMWRITE_JPEG_QUALITY, 70);


    public static void main(String[] args) throws InterruptedException {
        if (args.length != 7) {
            System.out.println("Usage:java main fpga|cpu input_file|input_dir output_file|output_dir thumb_width thumb_eight thread round");
            return;
        }

        // Get parameters
        final String useFpga = args[0];
        final File inPath = new File(args[1]);
        final File outPath = new File(args[2]);
        final int thumbWidth = Integer.parseInt(args[3]);
        final int thumbHeight = Integer.parseInt(args[4]);
        final int threadCount = Integer.parseInt(args[5]);
        final int round = Integer.parseInt(args[6]); // 30

        if (outPath.exists()) {
            outPath.delete();
        }
        outPath.mkdirs();

        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        pool.prestartAllCoreThreads();


        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {

            tasks.add(new Callable<Integer>() {
                @Override
                public Integer call() {
                    run_test_case(useFpga, inPath, outPath, round, thumbWidth, thumbHeight);
                    return 1;
                }
            });
        }
        long start = System.currentTimeMillis();
        pool.invokeAll(tasks);
        pool.shutdown();
        long cost = System.currentTimeMillis() - start;

        try {
            pool.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("cps = " + threadCount * round * inPath.list().length * 1000.0 / cost);
        System.out.println("latency = " + (float) calcCost.get() / round / inPath.list().length);
    }

    public static void run_test_case(String useFpga, final File inPath, final File outPath, int round, final int dstWidth, final int dstHeight) {

        ArrayList<Pair<String, String>> files = new ArrayList<>();
        for (String file : inPath.list()) {
            String in = inPath.getAbsolutePath() + "/" + file;
            String out = outPath.getAbsolutePath() + "/" + file + Thread.currentThread().getId() + "_thumb.jpg";
            files.add(Pair.of(in, out));
            System.out.println("add " + in + "," + out);
        }

        for (int j = 0; j < round; j++) {
            for (Pair<String, String> pair : files) {

                String orig = pair.getLeft();
                String outThumb = pair.getRight();

                Mat inImg = null;
                Mat outImg = null;
                try {
                    long start = System.currentTimeMillis();
                    Size size = Imgcodecs.imsize(orig);

                    Size dst = calculateSize((int) size.width, (int) size.height, dstWidth, dstHeight);

                    if (useFpga.equals("fpga")) {
                        outImg = Imgcodecs.imread(orig, (int) dst.width, (int) dst.height, Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
                    } else {
                        inImg = Imgcodecs.imread(orig);
                        outImg = new Mat();
                        Imgproc.resize(inImg, outImg, dst);
                    }

                    Imgcodecs.imwrite(outThumb, outImg, thumbQualityParams);

                    //PerfCounter.count("perf_" + useFpga, 1, System.currentTimeMillis() - start);
                    calcCost.getAndAdd(System.currentTimeMillis() - start);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (inImg != null) {
                        inImg.release();
                    }
                    if (outImg != null) {
                        outImg.release();
                    }
                }
            }
        }
    }

    public static Size calculateSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        if (srcWidth <= 0 || srcHeight <= 0 || dstWidth <= 0 || dstHeight <= 0) {
            throw new IllegalStateException(String.format("invalid width or height, src %d %d, dst %d %d", srcWidth, srcHeight, dstWidth, dstHeight));
        }

        if (srcWidth <= dstWidth || srcHeight <= dstHeight) {
            System.out.println("use original size, width: " + srcWidth + ", height: " + srcHeight);
            return new Size(srcWidth, srcHeight);
        }

        double r1 = (double) srcWidth / dstWidth;
        double r2 = (double) srcHeight / dstHeight;

        int width;
        int height;
        if (r1 < r2) { //r1 < r2, keep dstWidth and scale height
            width = dstWidth;
            height = dstWidth * srcHeight / srcWidth;
        } else {
            height = dstHeight;
            width = dstHeight * srcWidth / srcHeight;
        }

        return new Size(width, height);
    }
}
