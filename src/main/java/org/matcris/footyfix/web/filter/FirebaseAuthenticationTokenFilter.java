package org.matcris.footyfix.web.filter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class FirebaseAuthenticationTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String authToken = request.getHeader("Authorization");
        if (authToken != null) {
            try {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(authToken);
                String uid = decodedToken.getUid();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    uid,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (FirebaseAuthException e) {
                logger.error("Failed to authenticate", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
