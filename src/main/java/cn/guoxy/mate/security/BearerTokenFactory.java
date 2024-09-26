package cn.guoxy.mate.security;

import cn.guoxy.mate.account.dto.LoginUser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class BearerTokenFactory {
  private static final RSAKey RSA_JWK = KeyGeneratorUtils.generateRsa();

  private static final Duration TIME_TO_LIVE = Duration.ofMinutes(30);

  public static String generateToken(LoginUser loginUser) {
    try {
      Instant issuedAt = Instant.now();
      Instant expiresAt = issuedAt.plus(TIME_TO_LIVE);
      JWTClaimsSet claimsSet =
          new Builder()
              .subject(loginUser.getId())
              .audience(loginUser.getUsername())
              .issuer("Mate")
              .issueTime(new Date(issuedAt.toEpochMilli()))
              .expirationTime(new Date(expiresAt.toEpochMilli()))
              .notBeforeTime(new Date(issuedAt.toEpochMilli()))
              .claim("role", loginUser.getRole())
              .build();
      JWSSigner signer = new RSASSASigner(RSA_JWK.toRSAPrivateKey());
      SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
      jwt.sign(signer);
      return jwt.serialize();
    } catch (JOSEException e) {
      throw new BearerTokenAuthenticationException("生成令牌失败", e);
    }
  }

  public static LoginUser parseToken(String token) throws BearerTokenAuthenticationException {
    try {
      SignedJWT jwt = SignedJWT.parse(token);
      Date now = new Date();
      Date notBeforeTime = jwt.getJWTClaimsSet().getNotBeforeTime();
      if (now.before(notBeforeTime)) {
        throw new BearerTokenAuthenticationException("令牌过早使用");
      }
      Date expirationTime = jwt.getJWTClaimsSet().getExpirationTime();
      if (now.after(expirationTime)) {
        throw new BearerTokenAuthenticationException("令牌过期");
      }
      JWSVerifier verifier = new RSASSAVerifier(RSA_JWK.toRSAPublicKey());
      if (!jwt.verify(verifier)) {
        throw new BearerTokenAuthenticationException("令牌无效");
      }
      LoginUser user = new LoginUser();
      user.setId(jwt.getJWTClaimsSet().getSubject());
      user.setUsername(jwt.getJWTClaimsSet().getIssuer());
      user.setPassword(null);
      user.setRole(jwt.getJWTClaimsSet().getStringListClaim("role"));
      return user;
    } catch (JOSEException | ParseException e) {
      throw new BearerTokenAuthenticationException("解析令牌出错", e);
    }
  }
}
