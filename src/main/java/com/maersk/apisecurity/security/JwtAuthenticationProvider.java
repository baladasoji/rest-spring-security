package com.maersk.apisecurity.security;


import com.maersk.apisecurity.security.exception.JwtTokenMalformedException;
import com.maersk.apisecurity.security.model.AuthenticatedUser;
import com.maersk.apisecurity.security.model.JwtAuthenticationToken;
import com.maersk.apisecurity.security.transfer.JwtUserDto;
import com.maersk.apisecurity.security.util.USITokenValidator;
import com.maersk.apisecurity.security.util.AzureTokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.JOSEException;

import java.util.List;

/**
 * Used for checking the token from the request and supply the UserDetails if the token is valid
 *
 * @author pascal alma
 */
@Component
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private USITokenValidator usiTokenValidator;

    @Autowired
    private AzureTokenValidator azureTokenValidator;

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String token = jwtAuthenticationToken.getToken();
        //Initialize an emtpy authority list useful in case roles are not returned by the provider
        List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
        JwtUserDto parsedUser = null;

//Try Azure AD first and then try USI
        try{

         parsedUser = azureTokenValidator.parseToken(token);
        }
        catch (BadJOSEException bje)
        {
          System.out.println ("Got Base JOSE Exception will now try to parse from USI");
          bje.printStackTrace(System.err);
          parsedUser = usiTokenValidator.parseToken(token);
        }
        catch (JOSEException je)
        {
          je.printStackTrace (System.err);
        }


        if (parsedUser == null) {
            throw new JwtTokenMalformedException("JWT token is not valid");
        }

      //  List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(parsedUser.getRole());
      // USI and Azure curently provide roles in 2 different formats
      //USI provides in a space separated String
      // Azure provides in a String Array .. It is expected that USI will also provide roles in an array after the next patch release
      if (parsedUser.getRoles() != null)
      {
         authorityList = getAuthorityList(parsedUser.getRoles());
      }
      else if (parsedUser.getRole() != null){
        authorityList = getAuthorityList(parsedUser.getRole());
      }


        return new AuthenticatedUser(parsedUser.getId(), parsedUser.getUsername(), token, authorityList);
    }

    protected List<GrantedAuthority> getAuthorityList(String rolelist){
      String[] roles = rolelist.split(" ");
      List<GrantedAuthority> gas = new ArrayList<GrantedAuthority>();
      for (String r: roles){
        gas.add(new SimpleGrantedAuthority("ROLE_"+r));

      }
      return gas;

    }

    protected List<GrantedAuthority> getAuthorityList(String[] rolelist){
      List<GrantedAuthority> gas = new ArrayList<GrantedAuthority>();
      for (String r: rolelist){
        gas.add(new SimpleGrantedAuthority("ROLE_"+r));

      }
      return gas;

    }

}
