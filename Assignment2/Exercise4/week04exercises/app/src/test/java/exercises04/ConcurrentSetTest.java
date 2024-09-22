package exercises04;

import org.junit.jupiter.api.BeforeEach;

import java.lang.annotation.Repeatable;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.api.Assertions.*;
// TODO: Very likely you need to expand the list of imports

public class ConcurrentSetTest {

    // Variable with set under test
    private ConcurrentIntegerSet set;
    CyclicBarrier barrier;

    // TODO: Very likely you should add more variables here


    // Uncomment the appropriate line below to choose the class to
    // test
    // Remember that @BeforeEach is executed before each test
    @BeforeEach
    public void initialize() {
        // init set
        // set = new ConcurrentIntegerSetBuggy();
        set = new ConcurrentIntegerSetSync();
        // set = new ConcurrentIntegerSetLibrary();
    }

    // TODO: Define your tests below

    
    @RepeatedTest(5000)
    public void testAdd() {
        int nrThreads = 16;
        int threadElements = 100;
        barrier = new CyclicBarrier(nrThreads+1);
        for (int i = 0; i < nrThreads; i++) {
            final int threId = i;
            new Thread(() -> {
                try {
                    barrier.await();
                    for (int j = 0; j < threadElements; j++)
                    set.add(1); // (1)
                    barrier.await();
                }catch (InterruptedException | BrokenBarrierException err){

            }
                
            }).start();
        }
        try{
                barrier.await(); // Main thread waits for every thread to be ready
                barrier.await(); // Main thread waits for every thread to be finished

        }catch (InterruptedException | BrokenBarrierException err){

        }
        System.out.println(set.size());
        assertTrue(set.size()==1);
    }
    

    @RepeatedTest(5000)
    public void testRemove() {
        int nrThreads = 16;
        int threadElements = 100;
        for (int k = 0; k < nrThreads * threadElements; k++){
            set.add(k);
        }
        barrier = new CyclicBarrier(nrThreads+1);
        for (int i = 0; i < nrThreads; i++) {
            final int threId = i;
            new Thread(() -> {
                try {
                    barrier.await();
                    for (int j = 0; j < threadElements; j++)
                    set.remove(threId * threadElements + j); // (1)
                    barrier.await();
                }catch (InterruptedException | BrokenBarrierException err){

            }
                
            }).start();;
        }
        try{
                barrier.await(); // Main thread waits for every thread to be ready
                barrier.await(); // Main thread waits for every thread to be finished

        }catch (InterruptedException | BrokenBarrierException err){

        }
        System.out.println(set.size());
        assertTrue(set.size()==0);
    }

}
