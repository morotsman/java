package com.github.morotsman.java_playground.async_http;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
public class MockController {
    
    
    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="delay", defaultValue="0") int delay) {
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MockController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Hello, the delay was " + delay + " sec";
    }
    
}
