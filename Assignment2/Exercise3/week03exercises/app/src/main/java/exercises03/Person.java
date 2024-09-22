package exercises03;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

public class Person {
    private static long nextID = 0;

    private final long id;
    private String name, address;
    private int zip;

    public Person() {
        synchronized(Person.class) {
            id = nextID++;
        }
    }

    public Person(int initialID) {
        synchronized(Person.class) {
            if(nextID == 0) {
                System.out.println("Person: Setting initial nextID to: (" + initialID + ")");
                nextID = initialID;
            }
            id = nextID++;
        }
    }
    
    public synchronized String getName() { return name; }
    public synchronized int getZip() { return zip; }
    public synchronized String getAddress() { return address; }
    public synchronized long getID() { return id; }

    public synchronized void setZipAddress(int zip, String address) {
        this.address = address;
        this.zip = zip;
    }

    public synchronized void setName(String name) { this.name = name; }

    public synchronized String toString() {
        return "{ id = " + this.getID()
                + ", name = " + this.getName()
                + ", address = (" + this.getZip() + ", " + this.getAddress() + ")"
                + "}";
    }

    public static void main(String[] args) {
        final var CONSUMER_THREADS = 2;
        final var PRODUCER_THREADS = 2;
        final var THREAD_COUNT = CONSUMER_THREADS + PRODUCER_THREADS;

        final var MAX_ITER = 5;
        final var threads = new Thread[THREAD_COUNT];
        final var startBarrier = new CyclicBarrier(THREAD_COUNT + 1); //+1 for main thread
        final var workQ = new LinkedBlockingQueue<Person>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            var tid = i;

            if(i < CONSUMER_THREADS)
            {
                threads[i] = new Thread(() -> {
                    Random r = new Random();
    
                    try {
                        startBarrier.await();
                    } catch(Exception e) { e.printStackTrace(); }
    
                    for (int j = 0; j < MAX_ITER; j++) {
                        Person p;
                        var rand = r.nextInt(100);
                        if(rand > 75)   { p = new Person(rand); } 
                        else            { p = new Person(); }
                        
                        try {
                            workQ.put(p);
                        } catch(Exception e) { e.printStackTrace(); }
                        
                        if(rand < 50) {
                            p.setZipAddress(rand, "Strandvej");
                        }
                        System.out.println("Thread[" + tid + "] created: " + p.toString() );
                    }
                });
                threads[i].start();
            }
            else 
            {
                threads[i] = new Thread(() -> {
                    try {
                        startBarrier.await();
                    } catch(Exception e) { e.printStackTrace(); }
    
                    for (int j = 0; j < MAX_ITER; j++) {
                        try {
                            var p = workQ.take();
                            // p.setZipAddress(1000, "reader was here");
                            p.setName("Reader here");
                            System.out.println("Thread[" + tid + "] read:    " + p.toString());
                        } catch (Exception e) { e. printStackTrace();}
                    }
                });
                threads[i].start();
            }
        }

        try { startBarrier.await(); }
        catch(Exception e) { e.printStackTrace(); }

        for (int i = 0; i < THREAD_COUNT; i++)  {
            try { threads[i].join(); }
            catch(Exception e) { e.printStackTrace(); }
        }

        System.out.println("Main done - terminating");
    }
}
