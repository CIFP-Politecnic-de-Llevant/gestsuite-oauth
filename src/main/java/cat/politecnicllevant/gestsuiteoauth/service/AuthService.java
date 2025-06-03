package cat.politecnicllevant.gestsuiteoauth.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface AuthService {
    String getVerifiedToken(String token) throws GeneralSecurityException, IOException;
}
