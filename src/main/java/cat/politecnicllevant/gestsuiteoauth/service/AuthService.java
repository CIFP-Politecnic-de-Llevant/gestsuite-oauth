package cat.politecnicllevant.gestsuiteoauth.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface AuthService<T> {
    T verifyUser(String token) throws GeneralSecurityException, IOException;
}
