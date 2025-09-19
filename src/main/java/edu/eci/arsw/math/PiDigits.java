package edu.eci.arsw.math;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class that implements a parallel solution for the BBP Formula
 * Resource: https://en.wikipedia.org/wiki/Bailey%E2%80%93Borwein%E2%80%93Plouffe_formula
 * 
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2025-09-19
 */

public class PiDigits {

    private static final int digitsPerSum = 8;
    private static final double epsilon = 1e-17;

    /**
     * Calculates the sum of 16^(n - k)/(8 * k + m) from 0 to k
     * 
     * @param m Up bound"
     * @param n Low bound
     * @return the sum of 16^(n - k)/(8 * k + m) from 0 to k
     */
    public static byte[] getDigits(int start, int count, int numThreads) throws InterruptedException, IOException {
        if (start < 0 || count < 0 || numThreads <= 0) {
            throw new RuntimeException("Invalid Interval or Thread Count");
        }

        int rangePerThread = count / numThreads;
        int remaining = count % numThreads;

        List<PiDigitsThread> threads = new ArrayList<>();
        int currentStart = start;

        for (int i = 0; i < numThreads; i++) {
            int currentCount = rangePerThread + (i < remaining ? 1 : 0);
            PiDigitsThread thread = new PiDigitsThread(i, currentStart, currentCount);
            threads.add(thread);
            currentStart += currentCount;
        }

        for (PiDigitsThread thread : threads) {
            thread.start();
        }

        boolean allThreadsFinished = false;

        while (!allThreadsFinished) {

            Thread.sleep(5000);

            for (PiDigitsThread thread : threads) {
                thread.pauseThread();
            }
            System.out.println("Paused all threads. Press Enter to continue...");
            System.in.read();

            for (PiDigitsThread thread : threads) {
                thread.resumeThread();
            }

            allThreadsFinished = true;
            
            for (PiDigitsThread thread : threads) {
                if (thread.isAlive()) {
                    allThreadsFinished = false;
                    break;
                }
            }
        }

        byte[] result = new byte[count];
        int index = 0;
        for (PiDigitsThread thread : threads) {
            System.arraycopy(thread.getResult(), 0, result, index, thread.getResult().length);
            index += thread.getResult().length;
        }

        System.out.println("Calculation completed. Result:");
        // for (byte b : result) {
        //     System.out.print(b + " ");
        // }
        // System.out.println();

        return result;
    }

    /**
     * Returns a range of hexadecimal digits of pi
     * 
     * @param start The starting location of the range
     * @param count The number of digits to return
     * @return An array containing the hexadecimal digits
     */
    public static double sum(int m, int n) {
        if (m <= 0) {
            return 0;
        }
    
        double sum = 0;
        int d = m;
        int power = n;
    
        while (true) {
            double term;
    
            if (power > 0) {
                term = (double) hexExponentModulo(power, d) / d;
            } else {
                term = Math.pow(16, power) / d;
                if (term < epsilon) {
                    break;
                }
            }
    
            sum += term;
            power--;
            d += 8;
        }
    
        return sum;
    }

    private static int hexExponentModulo(int p, int m) {
        if (m == 0) {
            return 0;
        }
    
        int power = 1;
        while (power * 2 <= p) {
            power *= 2;
        }
    
        int result = 1;
    
        while (power > 0) {
            if (p >= power) {
                result *= 16;
                result %= m;
                p -= power;
            }
    
            power /= 2;
    
            if (power > 0) {
                result *= result;
                result %= m;
            }
        }
    
        return result;
    }

}
