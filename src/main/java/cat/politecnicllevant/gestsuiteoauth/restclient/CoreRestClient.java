package cat.politecnicllevant.gestsuiteoauth.restclient;

import cat.politecnicllevant.gestsuiteoauth.dto.UsuariDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "core")
public interface CoreRestClient {
    @GetMapping("/usuari/profile/{email}")
    ResponseEntity<UsuariDto> getPublicProfileByEmail(@PathVariable("email") String email);

    @GetMapping("/usuaris/llistat")
    ResponseEntity<List<UsuariDto>> getAllUsuaris();
}