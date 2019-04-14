package pl.dopierala.allegroreporeaderapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    RepoService repoService;

    @Autowired
    public Controller(RepoService repoService) {
        this.repoService = repoService;
    }

    @GetMapping("getRepos")
    public void getRepos(@RequestParam String user){
        repoService.getUserRepos(user);
    }
}
