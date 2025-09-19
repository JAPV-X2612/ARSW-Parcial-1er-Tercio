package edu.eci.arsw.math;

public class PiDigitsThread extends Thread {

    private final int threadId;
    private final int startRange;
    private final int count;
    private final byte[] result;
    private boolean paused;

    public PiDigitsThread(int threadId, int startRange, int count) {
        this.threadId = threadId;
        this.startRange = startRange;
        this.count = count;
        this.result = new byte[count];
        this.paused = false;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < count; i++) {
                synchronized (this) {
                    while (paused) {
                        wait();
                    }
                }
                
                result[i] = (byte) PiDigits.sum(startRange + i, 8 * i + 1);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread " + threadId + " was interrupted");
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void pauseThread() {
        paused = true;
    }

    public synchronized void resumeThread() {
        paused = false;
        notify();
    }

    public byte[] getResult() {
        return result;
    }

}
