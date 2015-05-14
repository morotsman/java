package com.github.morotsman.java_playground.async_http;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
public class HelloWorld {
    
    public static void main(String... args) throws InterruptedException{
        System.out.println("Hello world");
        
        final long startTime = System.currentTimeMillis();
        
        final int numberOfThreads = 2000;
        
        ExecutorService ex = Executors.newFixedThreadPool(numberOfThreads);
       
        
        List<CompletableFuture<String>> futures = 
                IntStream.
                        range(0, numberOfThreads).
                        boxed().
                        map(n -> CompletableFuture.supplyAsync(() -> {
                            try {
                                Thread.sleep(20000);
                            } catch (InterruptedException ex1) {
                                Logger.getLogger(HelloWorld.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                            return n;
                        }, ex)).
                        map(f -> f.thenApply(n -> "Task number " + n)).
                        collect(Collectors.toList());
        
        List<String> results = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        
        ex.shutdown();
        
        results.forEach(System.out::println);
        
        
        System.out.println("Completed in: " + (System.currentTimeMillis() - startTime)/1000 + " seconds.");
        
    }
    
}
