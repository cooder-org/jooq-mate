package org.cooder.jooq.mate.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    /**
     * welcome hello method
     * 
     * @return "hello"
     */
    @GetMapping("/api/welcome/hello")
    public String hello() {
        return "hello";
    }
}
