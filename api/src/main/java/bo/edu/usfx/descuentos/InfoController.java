package bo.edu.usfx.descuentos;

import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/info: que version y que commit estan corriendo.
 * El smoke test del pipeline lo usa para comprobar que el despliegue es el del commit actual.
 */
@RestController
@RequestMapping("/api")
public class InfoController {

    private final String version;
    private final String commit;

    public InfoController(ObjectProvider<BuildProperties> build, @Value("${app.commit}") String commit) {
        BuildProperties props = build.getIfAvailable();
        this.version = props != null ? props.getVersion() : "dev";
        this.commit = commit;
    }

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of("app", "descuentos-api", "version", version, "commit", commit);
    }
}
