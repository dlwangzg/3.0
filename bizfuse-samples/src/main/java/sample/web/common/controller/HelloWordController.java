package sample.web.common.controller;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.leadingsoft.bizfuse.common.web.support.Searchable;

@RestController
@RequestMapping("/api")
public class HelloWordController {

    private final Logger log = LoggerFactory.getLogger(HelloWordController.class);

    @Timed
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public ResponseEntity<String> helloworld(final Searchable searchable) throws URISyntaxException {
        this.log.debug("REST request helloworld");
        System.out.println(searchable.getStrValue("user"));
        return new ResponseEntity<>("HelloWorld", HttpStatus.OK);
    }
}
