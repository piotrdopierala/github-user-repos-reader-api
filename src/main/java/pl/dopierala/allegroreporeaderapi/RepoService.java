package pl.dopierala.allegroreporeaderapi;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.dopierala.allegroreporeaderapi.Model.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class RepoService {
    private static final String GITHUB_API_URL = "https://api.github.com/"; //todo reads only 30 repos even if user has more.
    private static final String GITHUB_API_USER_REPOS = "users/%s/repos";

    private static final String RESP_FIELD_FULL_NAME = "full_name";
    private static final String RESP_FIELD_URL = "url";
    private static final String RESP_FIELD_DESCRIPTION = "description";
    private static final String RESP_FIELD_CREATED_AT = "created_at";

    public List<Repository> getUserRepos(String userName) {
        final String url = String.format(GITHUB_API_URL + GITHUB_API_USER_REPOS, userName);
        List<Repository> fetchedRepos = new ArrayList<>();

        RestTemplate rt = new RestTemplate();
        String receivedReposString = rt.getForObject(url, String.class);

        JSONArray reposJsonArray = new JSONArray(receivedReposString);

        for (int i = 0; i < reposJsonArray.length(); i++) {
            JSONObject repoJson = reposJsonArray.getJSONObject(i);
            fetchedRepos.add(parseRepository(repoJson));
        }
        return fetchedRepos;
    }

    private Repository parseRepository(JSONObject json) { //todo can throw JSONException, handle on page with AOP
        Repository repo = new Repository();

        if (json.has(RESP_FIELD_FULL_NAME) && !json.isNull(RESP_FIELD_FULL_NAME))
            repo.setName(json.getString(RESP_FIELD_FULL_NAME));
        if (json.has(RESP_FIELD_DESCRIPTION) && !json.isNull(RESP_FIELD_DESCRIPTION))
            repo.setDescription(json.getString(RESP_FIELD_DESCRIPTION));
        if (json.has(RESP_FIELD_URL) && !json.isNull(RESP_FIELD_URL))
            repo.setUrl(json.getString(RESP_FIELD_URL));
        if (json.has(RESP_FIELD_CREATED_AT) && !json.isNull(RESP_FIELD_CREATED_AT))
            repo.setCreatedAt(LocalDateTime.ofInstant(Instant.parse(json.getString(RESP_FIELD_CREATED_AT)), ZoneId.of("Europe/Warsaw"))); //todo uwzglednic TimeZone uzytkownika

        return repo;
    }

}
