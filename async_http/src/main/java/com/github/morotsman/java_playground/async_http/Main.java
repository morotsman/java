package com.github.morotsman.java_playground.async_http;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Main { 
    
    //ExecutorService es = Executors.newFixedThreadPool(10);
    //AsyncHttpClientConfig cf = new AsyncHttpClientConfig.Builder().setExecutorService(es).build();
    //AsyncHttpClient asyncHttpClient = new AsyncHttpClient(cf);
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    public void start() throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        System.out.println("Start begin");
        
        Stream<String> urls = IntStream
                .range(0, 1000)
                .boxed()
                .sorted(Collections.reverseOrder())
                .map(n -> "http://localhost:8080/greeting?delay=" + randInt(0,2))
                .peek(System.out::println);
        
        
        List<CompletableFuture<String>> bodies = 
                urls
                .map(url -> getPage(url))
                .collect(Collectors.toList());
        
        System.out.println("All requests sent");

        CompletableFuture[] futures = bodies.stream().map(f -> f.thenAccept(System.out::println)).toArray(size -> new CompletableFuture[size]);
        
        CompletableFuture.allOf(futures).join();

        System.out.println("Completed in: " + (System.currentTimeMillis() - startTime)/1000);

        asyncHttpClient.close();

    }

    public int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private CompletableFuture<String> getPage(String page) {

        CompletableFuture result = new CompletableFuture();

        asyncHttpClient.prepareGet(page).execute(
                new AsyncCompletionHandler<Response>() {

                    @Override
                    public Response onCompleted(Response response) throws Exception {
                        result.complete(response.getResponseBody());
                        return response;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        result.completeExceptionally(t);
                    }
                });
        return result;
    }

    public static void main(String... args) throws InterruptedException, ExecutionException {
        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        Main main = new Main();
        main.start();

    }

}
