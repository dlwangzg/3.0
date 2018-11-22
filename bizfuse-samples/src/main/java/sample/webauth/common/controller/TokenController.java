package sample.webauth.common.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.leadingsoft.bizfuse.common.webauth.access.CurrentUserBean;
import com.leadingsoft.bizfuse.common.webauth.access.DefaultAuthenticationToken;
import com.leadingsoft.bizfuse.common.webauth.access.SimpleGrantedAuthority;
import com.leadingsoft.bizfuse.common.webauth.annotation.CurrentUser;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.TokenProvider;

@RestController
@RequestMapping("/api")
public class TokenController {

    private final Logger log = LoggerFactory.getLogger(TokenController.class);
    @Autowired
    private TokenProvider tokenProvider;

    @Timed
    @RequestMapping(value = "/checkToken", method = RequestMethod.GET)
    public ResponseEntity<String> helloworld(@RequestHeader("Authorization") final String token,
            @CurrentUser final CurrentUserBean user) {
        this.log.info("REST request checkToken login user {}", user.getUserPrincipal());
        System.out.println(token);
        return new ResponseEntity<>("HelloWorld", HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public ResponseEntity<ModelMap> token(@RequestParam final String username, @RequestParam final String role) {
        this.log.debug("REST request to create token");
        final ModelMap model = new ModelMap();

        final DefaultAuthenticationToken token = new DefaultAuthenticationToken();
        token.setAuthenticated(true);
        Collection<SimpleGrantedAuthority> auths = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        token.setAuthorities(auths);
        final Map<String, String> details = new HashMap<>();
        details.put("orgType", "省委");
        token.setDetails(details);
        token.setPrincipal(username);
        model.put("token", this.tokenProvider.createToken(token, false));
        return new ResponseEntity<>(model, HttpStatus.OK);
    }
}
