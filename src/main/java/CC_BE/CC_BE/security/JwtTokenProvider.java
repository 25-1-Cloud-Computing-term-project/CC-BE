package CC_BE.CC_BE.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private final CustomUserDetailsService userDetailsService;

    public JwtTokenProvider(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public String generateToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("role", userPrincipal.getUser().getRole())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserEmailFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Authentication getAuthentication(String token) {
        String userEmail = getUserEmailFromJWT(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            throw new SecurityException("유효하지 않은 JWT 서명입니다.");
        } catch (MalformedJwtException ex) {
            throw new SecurityException("유효하지 않은 JWT 토큰입니다.");
        } catch (ExpiredJwtException ex) {
            throw new SecurityException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException ex) {
            throw new SecurityException("지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException ex) {
            throw new SecurityException("JWT 토큰이 비어있습니다.");
        }
    }
} 