package exercises03;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class BoundedBuffer<T> implements BoundedBufferInteface<T> {
    
    private final LinkedList<T> buffer = new LinkedList<T>();
    private final Semaphore buffMutex = new Semaphore(1);
    private final Semaphore readSema;
    private final Semaphore writeSema;
    

    public BoundedBuffer(int bufferSize) {
        this.writeSema = new Semaphore(bufferSize, true);
        this.readSema  = new Semaphore(bufferSize, true);
        this.readSema.drainPermits();
    }

    public T take() throws Exception {
        readSema.acquire();

        buffMutex.acquire();
        var elm = buffer.removeFirst();
        buffMutex.release();

        writeSema.release();
        return elm;
    }
    
    /*
     * Consumer: 1 => buffer is empty
     * Producer: 0 => buffer is full
     * 
     * Consumer: 0 => buffer is full
     * Producer: 1 => buffer is empty
     */

    public void insert(T elem) throws Exception {
        writeSema.acquire();

        buffMutex.acquire();
        buffer.add(elem);
        buffMutex.release();
        
        readSema.release();
        
        //writeSema.release();
    }


    public static void main(String[] args) {
        var buffer = new BoundedBuffer<Integer>(5);
        final int ITERS = 10;
        
        var threadCount = 10;
        var threads = new Thread[threadCount * 2];

        for (int j = 0; j < threadCount; j++) {
            int t = j;
            Thread producer2 = new Thread(() -> {
                for (int i = (t*ITERS); i < ((t+1)*ITERS); i++) {
                    try {
                        buffer.insert(i);
                        System.out.println("Producer inserted: " + i);
                    } catch(Exception e) {
                        System.err.println(e);
                    }
                }
            });
            threads[j] = producer2;
        }

        for (int j = threadCount; j < threadCount*2; j++) {
            Thread consumer = new Thread(() -> {
                for(int i = 0; i < ITERS; i++){
                    try {
                        var out = buffer.take();
                        System.out.println("Consumer took: " + out);
                    } catch(Exception e) {
                        System.err.println(e);
                    }
                }
            });
            threads[j] = consumer;
        }
       
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch(Exception e) { }
        }

        System.out.println("Yo");
    }

}
