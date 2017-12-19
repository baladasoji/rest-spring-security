package com.maersk.apisecurity.security.util;
import com.nimbusds.jwt.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.*;
import com.maersk.apisecurity.security.transfer.JwtUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.PublicKey;
import java.io.IOException;
import java.text.ParseException;

/**
 * Class validates a given token by using the secret configured in the application
 *
 * @author pascal alma
 */
@Component
public class NimbusTokenValidator {



    @Value("${jwt.secret}")
    private String secret;

    /**
     * Tries to parse specified String as a JWT token. If successful, returns User object with username, id and role prefilled (extracted from token).
     * If unsuccessful (token is invalid or not containing all required user properties), simply returns null.
     *
     * @param token the JWT token to parse
     * @return the User object extracted from specified token or null if a token is invalid.
     */
    public JwtUserDto parseToken(String token) {
        JwtUserDto u = null;

        try {
          PublicKey key = PemUtils.readPublicKeyFromFile("pubkey.pem", "RSA");

          JWT jot = JWTParser.parse(token) ;
          JWTClaimsSet claims = jot.getJWTClaimsSet();
            u = new JwtUserDto();
            u.setUsername(claims.getSubject());
            u.setId(Long.parseLong("123"));
            u.setRole((String) claims.getClaim("roles"));
        /*    Claims body = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();

            u = new JwtUserDto();
            u.setUsername(body.getSubject());
            u.setId(Long.parseLong("123"));
            //u.setId(Long.parseLong((String) body.get("123")));
            u.setRole((String) body.get("roles"));

            */

        } catch (ParseException e) {
            // Simply print the exception and null will be returned for the userDto
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return u;
    }
}
