package com.maersk.apisecurity.security.util;
import com.nimbusds.jose.*;
import com.nimbusds.jose.jwk.source.*;
import com.nimbusds.jwt.*;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jwt.proc.*;
import com.maersk.apisecurity.security.transfer.JwtUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.PublicKey;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.net.URL;

/**
 * Class validates a given token by using the secret configured in the application
 *
 * @author pascal alma
 */
@Component
public class AzureTokenValidator {


    @Value("${jwt.azurejwk}")
    private String azurejwk;

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Tries to parse specified String as a JWT token. If successful, returns User object with username, id and role prefilled (extracted from token).
     * If unsuccessful (token is invalid or not containing all required user properties), simply returns null.
     *
     * @param token the JWT token to parse
     * @return the User object extracted from specified token or null if a token is invalid.
     */
    public JwtUserDto parseToken(String token) throws BadJOSEException, JOSEException {
        JwtUserDto u = null;

        try {

          ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();

          // The public RSA keys to validate the signatures will be sourced from the
          // OAuth 2.0 server's JWK set, published at a well-known URL. The RemoteJWKSet
          // object caches the retrieved keys to speed up subsequent look-ups and can
          // also gracefully handle key-rollover
          JWKSource keySource = new RemoteJWKSet(new URL(azurejwk));

          // The expected JWS algorithm of the access tokens (agreed out-of-band)
          JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;

          // Configure the JWT processor with a key selector to feed matching public
          // RSA keys sourced from the JWK set URL
          JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
          jwtProcessor.setJWSKeySelector(keySelector);

          // Process the token
          SecurityContext ctx = null; // optional context parameter, not required here
          JWTClaimsSet claims = jwtProcessor.process(token, ctx);

            u = new JwtUserDto();
            u.setUsername(claims.getStringClaim("name"));
            u.setId(Long.parseLong("123"));
            String[] roles = claims.getStringArrayClaim("roles");
            u.setRoles(roles);

        } catch (ParseException e) {
            // Simply print the exception and null will be returned for the userDto
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return u;
    }
}
