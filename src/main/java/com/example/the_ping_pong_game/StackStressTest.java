package com.example.the_ping_pong_game;

/**
 * Stack Stress Test to test
 * Maximum Recursion Depth,
 * Time (in ms) until StackOverflowError
 */
public class StackStressTest {
    static int count = 0;

    /**
     * infinitely keeps adding stack frames
     */
    public static void recursiveCall() {
        count++;
        if (count % 100 == 0) {
            System.out.println("Stack depth: " + count);
        }
        recursiveCall();
    }

    /**
     * Main Method - test Stack
     * Calls recursiveCall() until failure
     */
    public static void main(String[] args) {

        long start = System.nanoTime();

        try {
            recursiveCall();
        } catch (StackOverflowError e) {

            long end = System.nanoTime();

            System.out.println("\nSTACK LIMIT REACHED!");
            System.out.println("Max recursion depth: " + count);
            System.out.println("Time: " + (end - start) / 1_000_000 + " ms");
            System.out.println("Error: " + e);
        }
    }
}