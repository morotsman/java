package com.github.morotsman.java_playground.async_http;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Main {

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    public void start() throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        System.out.println("Start begin");
        
        Stream<String> urls = Stream.generate(() -> "http://localhost:8080/greeting?delay=" + randInt(0,5));
        
        List<CompletableFuture<String>> bodies = 
                urls
                .limit(1000)
                .map(url -> getPage(url)).collect(Collectors.toList());
        
        System.out.println("***");

        bodies.stream().parallel().forEach((CompletableFuture f) -> {
            try {
                System.out.println(f.get());
            } catch (InterruptedException | ExecutionException ex) {
                
            }
        });

        System.out.println("Start end: " + (System.currentTimeMillis() - startTime)/1000);

        asyncHttpClient.close();

    }

    public int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

    // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
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
