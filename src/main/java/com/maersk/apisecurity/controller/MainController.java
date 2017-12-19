package com.maersk.apisecurity.controller;

import com.maersk.apisecurity.model.Greeting;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.annotation.Secured;
import java.security.Principal;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Example controller to test security api calls
 */
@CrossOrigin(origins = "*")
@RestController
public class MainController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @PreAuthorize("hasRole('Booking')")
    @RequestMapping(value = "/booking", method = RequestMethod.GET)
    public Greeting booking(@RequestParam(value = "name", defaultValue = "You have access to booking") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @PreAuthorize("hasRole('WBoL-Print')")
    @RequestMapping(value = "/bol", method = RequestMethod.GET)
    public Greeting bol(@RequestParam(value = "name", defaultValue = "You have access to Bill of Lading") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @PreAuthorize("hasRole('Finance')")
    @RequestMapping(value = "/finance", method = RequestMethod.GET)
    public Greeting finance(@RequestParam(value = "name", defaultValue = "You have access to Finance") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @PreAuthorize("hasRole('Administrator')")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public Greeting admin(@RequestParam(value = "name", defaultValue = "You have access to admin") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

   @RequestMapping(value = {"/userdetails"}, method = RequestMethod.GET)
   public String user(Principal principal) {
       return principal.getName();
   }

}
