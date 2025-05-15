//package org.gamja.gamzatechblog.core.auth.jwt;
//
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//
//import javax.crypto.SecretKey;
//import java.util.Base64;
//
//public class JwtKeyGenerator {
//    public static void main(String[] args) {
//        // HS256용 랜덤 시크릿 키 생성
//        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//        // 바이트 배열을 Base64 문자열로 변환
//        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
//        System.out.println("생성된 Base64 시크릿 키:\n" + base64Key);
//    }
//}
