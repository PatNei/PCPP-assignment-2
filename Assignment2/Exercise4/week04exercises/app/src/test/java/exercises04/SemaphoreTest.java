package exercises04;

import java.util.concurrent.atomic.AtomicInteger;

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

import exercises04.SemaphoreImp;

public class SemaphoreTest {
    SemaphoreImp sem; 
    final Integer capacity = 10;
    AtomicInteger criticalSectionCount;
    AtomicInteger violationCount;
    CyclicBarrier barrier;

    @BeforeEach
    public void Initialize() {
        sem = new SemaphoreImp(capacity);
        criticalSectionCount = new AtomicInteger(0);
        violationCount = new AtomicInteger(0);
    }


    @ParameterizedTest
    @RepeatedTest(1000)
    public void capacityTest() {
        // Kan være vi lige skal lave noget med cyclic barrier
        sem.release(); // should be -1
        barrier = new CyclicBarrier(capacity + 1 + 1);

        for (int i = 0; i < capacity + 1; i++) { 
            new Thread(() -> {
                try {
                    barrier.await(); // testing boilerplate
                    sem.acquire(); // Acquire access to the critical section
                
                    var currentCount = criticalSectionCount.incrementAndGet(); 
                    // Current threads in the critical section
                    if (currentCount > capacity){
                        violationCount.incrementAndGet();
                    }
                    // To simulate work in the critical section
                    Thread.sleep(100); 
                    criticalSectionCount.decrementAndGet();
                    sem.release();
                    barrier.await();
                }catch (Exception e) {
                    // TODO: handle exception
                }
            }).start();
        }
        try{
                barrier.await(); // Main thread waits for every thread to be ready
                barrier.await(); // Main thread waits for every thread to be finished

        }catch (InterruptedException | BrokenBarrierException err){

        }
        System.out.println(violationCount.get());
        assertTrue(violationCount.get()==0);
        
    }

    
}
