package com.example.the_ping_pong_game;
import java.util.ArrayList;
import java.util.List;

/**
 * Heap Stress Test to test
 * Objects created before failure
 */
public class HeapStressTest {

    public static void main(String[] args) {

        // Storage list placed in HEAP
        List<int[]> memory = new ArrayList<>();

        int count = 0;

        try {
            while (true) {
                // Large object allocation (10,000 integers)
                int[] ints = new int[10000];
                memory.add(ints); // Keep reference → prevents GC
                count++;
                if (count % 100 == 0) {
                    System.out.println("Created: " + count);
                }
            }
        } catch (OutOfMemoryError e) {

            System.out.println("\nHEAP LIMIT REACHED!");
            System.out.println("Total objects created: " + count);
            System.out.println("Error: " + e);
        }
    }
}
