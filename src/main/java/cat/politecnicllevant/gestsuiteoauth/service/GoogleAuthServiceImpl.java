package cat.politecnicllevant.gestsuiteoauth.service;

import cat.politecnicllevant.gestsuiteoauth.dto.RolDto;
import cat.politecnicllevant.gestsuiteoauth.dto.UsuariDto;
import cat.politecnicllevant.gestsuiteoauth.exception.TokenErroriException;
import cat.politecnicllevant.gestsuiteoauth.restclient.CoreRestClient;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


//@Slf4j

@Service
public class GoogleAuthServiceImpl implements AuthService {

    @Value("${gc.adminUser}")
    private String administrador;

    @Value("${gc.adminDeveloper}")
    private String adminDeveloper;

    @Value("${gc.clientId}")
    private String clientId;

    @Autowired
    private CoreRestClient coreRestClient;

    @Autowired
    private TokenManager tokenManager;


    @Override
    public String getVerifiedToken(String token) throws GeneralSecurityException, IOException {
        //        log.info("Token:" + token);

        GoogleIdToken idToken = verifyUser(token);

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");
//            String locale = (String) payload.get("locale");
//            String familyName = (String) payload.get("family_name");
//            String givenName = (String) payload.get("given_name");

            UsuariDto usuari = coreRestClient.getPublicProfileByEmail(email).getBody();

            if (emailVerified && usuari != null) {
                System.out.println("create token with e-mail: " + email);
                Set<RolDto> rols = new HashSet<>();
                if (usuari.getRols() != null) {
                    rols.addAll(usuari.getRols());
                }

                if (usuari.getGestibProfessor() != null && usuari.getGestibProfessor()) {
                    rols.add(RolDto.PROFESSOR);
                }

                if (usuari.getGestibAlumne() != null && usuari.getGestibAlumne()) {
                    rols.add(RolDto.ALUMNE);
                }

                return tokenManager.createToken(email,rols.stream().map(Enum::toString).collect(Collectors.toList()),name);

            } else{
                List<UsuariDto> usuaris = coreRestClient.getAllUsuaris().getBody();

                //Comprovem si la BBDD és buida i es tracta de la primera càrrega
                //o bé és l'usuari desenvolupador
                if( (email.equals(this.administrador) && (usuaris == null || usuaris.isEmpty())) || email.equals(this.adminDeveloper)){
                    Set<RolDto> rols = new HashSet<>();
                    rols.add(RolDto.ADMINISTRADOR);

                    return tokenManager.createToken(email,rols.stream().map(Enum::toString).collect(Collectors.toList()),name);

                }
            }
        } else {
            throw new TokenErroriException("Token erroni");
        }

        return null;
    }

    private GoogleIdToken verifyUser(String token) throws GeneralSecurityException, IOException {
//        log.info("Token Google: "+token);
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        GoogleIdTokenVerifier tokenVerifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(this.clientId))
                .build();
        return tokenVerifier.verify(token);
    }
}

