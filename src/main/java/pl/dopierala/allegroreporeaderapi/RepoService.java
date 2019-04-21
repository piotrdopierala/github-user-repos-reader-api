package pl.dopierala.allegroreporeaderapi;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.dopierala.allegroreporeaderapi.Exceptions.ParseToJsonNotPossible;
import pl.dopierala.allegroreporeaderapi.Exceptions.UserNotFound;
import pl.dopierala.allegroreporeaderapi.Model.Repository;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RepoService {
    @Autowired
    private RestTemplate restTemplate;

    private static final String GITHUB_API_URL = "https://api.github.com/"; //todo reads only 30 repos even if user has more.
    private static final String GITHUB_API_USER_REPOS = "users/%s/repos";

    private static final String RESP_FIELD_FULL_NAME = "full_name";
    private static final String RESP_FIELD_URL = "html_url";
    private static final String RESP_FIELD_DESCRIPTION = "description";
    private static final String RESP_FIELD_CREATED_AT = "created_at";

    public List<Repository> getUserRepos(String userName) {//todo handle exception on page with AOP
        if (Objects.isNull(userName)) {
            return new ArrayList<>();
        }
        final String url = String.format(GITHUB_API_URL + GITHUB_API_USER_REPOS, userName);
        List<Repository> fetchedRepos = new ArrayList<>();

        String receivedReposString = "";

        try {
            receivedReposString = restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println(receivedReposString);
            throw new UserNotFound();
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode fetchedReposJson = null;
        try {
            fetchedReposJson = mapper.readTree(receivedReposString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < fetchedReposJson.size(); i++) {//todo throw parseToJsonNotPossible when wrong json received.
            JsonNode repoJson = fetchedReposJson.get(i);
            fetchedRepos.add(parseRepository(repoJson));
        }
        return fetchedRepos;
    }

    private Repository parseRepository(JsonNode json) {
        Repository repo = new Repository();

        if (json.has(RESP_FIELD_FULL_NAME))
            repo.setName(json.get(RESP_FIELD_FULL_NAME).asText());
        if (json.has(RESP_FIELD_DESCRIPTION))
            repo.setDescription(json.get(RESP_FIELD_DESCRIPTION).asText());
        if (json.has(RESP_FIELD_URL))
            repo.setUrl(json.get(RESP_FIELD_URL).asText());
        if (json.has(RESP_FIELD_CREATED_AT))
            repo.setCreatedAt(LocalDateTime.ofInstant(Instant.parse(json.get(RESP_FIELD_CREATED_AT).asText()), ZoneId.of("Europe/Warsaw"))); //todo uwzglednic TimeZone uzytkownika
        return repo;
    }

}
