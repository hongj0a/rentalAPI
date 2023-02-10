package dejay.rnd.billyG.jwt;

import dejay.rnd.billyG.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {

   private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
   private static final String AUTHORITIES_KEY = "auth";
   private final String secret;
   private long accessTokenValidTime = Duration.ofMinutes(300).toMillis(); // 만료시간 5시간
   private long refreshTokenValidTime = Duration.ofDays(30).toMillis(); // 만료시간 30일
   private Key key;

   public TokenProvider(
      @Value("${jwt.secret}") String secret) {
      this.secret = secret;
   }

   @Override
   public void afterPropertiesSet() {
      byte[] keyBytes = Decoders.BASE64.decode(secret);
      this.key = Keys.hmacShaKeyFor(keyBytes);
   }

   public TokenDto createToken(Authentication authentication) {
      String authorities = authentication.getAuthorities().stream()
         .map(GrantedAuthority::getAuthority)
         .collect(Collectors.joining(","));

      long now = (new Date()).getTime();
      Date aTvalidity = new Date(now + this.accessTokenValidTime);
      Date rTvalidity = new Date(now + this.refreshTokenValidTime);


      String accessToken = Jwts.builder()
              .setSubject(authentication.getName())
              .claim("auth", authorities)
              .setExpiration(aTvalidity)
              .signWith(key, SignatureAlgorithm.HS256)
              .compact();

      // Refresh Token 생성
      String refreshToken = Jwts.builder()
              .setSubject(authentication.getName())
              .claim("auth", authorities)
              .setExpiration(rTvalidity)
              .signWith(key, SignatureAlgorithm.HS256)
              .compact();

      return TokenDto.builder()
              .grantType("Bearer")
              .accessToken(accessToken)
              .refreshToken(refreshToken)
              .build();

   }

   public Authentication getAuthentication(String token) {
      Claims claims = Jwts
              .parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token)
              .getBody();

      Collection<? extends GrantedAuthority> authorities =
         Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

      User principal = new User(claims.getSubject(), "", authorities);

      return new UsernamePasswordAuthenticationToken(principal, token, authorities);
   }

   public boolean validateToken(String token) {
      try {
         Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
         return true;
      } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
         logger.info("잘못된 JWT 서명입니다.");
      } catch (ExpiredJwtException e) {
         logger.info("만료된 JWT 토큰입니다.");
      } catch (UnsupportedJwtException e) {
         logger.info("지원되지 않는 JWT 토큰입니다.");
      } catch (IllegalArgumentException e) {
         logger.info("JWT 토큰이 잘못되었습니다.");
      }
      return false;
   }

   private Claims parseClaims(String accessToken) {
      try {
         return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
      } catch (ExpiredJwtException e) {
         return e.getClaims();
      }
   }

}