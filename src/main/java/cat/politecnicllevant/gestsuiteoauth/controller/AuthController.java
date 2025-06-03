package cat.politecnicllevant.gestsuiteoauth.controller;

import cat.politecnicllevant.common.model.Notificacio;
import cat.politecnicllevant.common.model.NotificacioTipus;
import cat.politecnicllevant.gestsuiteoauth.dto.RolDto;
import cat.politecnicllevant.gestsuiteoauth.exception.TokenErroriException;
import cat.politecnicllevant.gestsuiteoauth.service.AuthService;
import cat.politecnicllevant.gestsuiteoauth.service.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    @Value("${public.password}")
    private String publicPassword;

    @Value("${gc.adminUser}")
    private String administrador;

    @Autowired
    private AuthService authService;

    @Autowired
    TokenManager tokenManager;

    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUserOauth(@RequestBody String token) throws GeneralSecurityException, IOException {

        System.out.println("Token: " + token);
        String verifiedToken;
        try {
            verifiedToken = authService.getVerifiedToken(token);

        } catch (TokenErroriException e) {
            Notificacio notificacio = new Notificacio();
            notificacio.setNotifyMessage("Token erroni");
            notificacio.setNotifyType(NotificacioTipus.ERROR);

            return new ResponseEntity<>(notificacio, HttpStatus.UNAUTHORIZED);
        }

        if (verifiedToken == null || verifiedToken.isEmpty()) {
            Notificacio notificacio = new Notificacio();
            notificacio.setNotifyMessage("Usuari no autoritzat!");
            notificacio.setNotifyType(NotificacioTipus.ERROR);

            return new ResponseEntity<>(notificacio, HttpStatus.UNAUTHORIZED);
        }

        ResponseCookie resCookie = ResponseCookie.from("hola", "adeu")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .domain("localhost")
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookie.toString()).body(verifiedToken);
    }

    @PostMapping("/auth/admin/token")
    public ResponseEntity<String> getToken(@RequestBody String password){
        if(password.equals(publicPassword)){
            Set<RolDto> rols = new HashSet<>();
            rols.add(RolDto.ADMINISTRADOR);
            return ResponseEntity.ok(tokenManager.createToken(administrador,rols.stream().map(Enum::toString).collect(Collectors.toList()),"admin"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
