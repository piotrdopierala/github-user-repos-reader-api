package pl.dopierala.allegroreporeaderapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dopierala.allegroreporeaderapi.Model.Repository;

import java.util.List;
import java.util.Objects;

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
                                                     @RequestHeader(name="clientTimeZoneOffset",required = false) String headerClientTimeZoneOffset) {
        int userTimeZoneOffset;
        if(Objects.isNull(headerClientTimeZoneOffset)){
            userTimeZoneOffset = 0;
        }else{
            userTimeZoneOffset = Integer.parseInt(headerClientTimeZoneOffset);
        }
        List<Repository> userRepos = repoService.getUserRepos(user,userTimeZoneOffset);
        return ResponseEntity.ok(userRepos);
    }
}
