package pl.dopierala.allegroreporeaderapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dopierala.allegroreporeaderapi.Model.Repository;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RepoController {

    RepoService repoService;

    @Autowired
    public RepoController(RepoService repoService) {
        this.repoService = repoService;
    }

    @GetMapping(path = "/getRepos/{user}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Repository>> getRepos(@PathVariable String user,
                                                     @RequestHeader(name="clientDateZone",required = false) String headerClientDateZone) {
        List<Repository> userRepos = repoService.getUserRepos(user);
        return ResponseEntity.ok(userRepos);
    }
}