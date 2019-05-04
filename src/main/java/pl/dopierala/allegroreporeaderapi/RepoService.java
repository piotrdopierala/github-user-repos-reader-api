package pl.dopierala.allegroreporeaderapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.dopierala.allegroreporeaderapi.Exceptions.ParseToJsonNotPossible;
import pl.dopierala.allegroreporeaderapi.Exceptions.UserNotFound;
import pl.dopierala.allegroreporeaderapi.Model.Repository;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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

    public static final int MINUTES_IN_HOUR = 60;

    public List<Repository> getUserRepos(String userName, int userTimeZoneOffset) {//todo handle exception on page with AOP
        if (Objects.isNull(userName)) {
            return new ArrayList<>();
        }
        final String path = String.format(GITHUB_API_URL + GITHUB_API_USER_REPOS, userName);
        URI targetUrl = UriComponentsBuilder.fromUriString(path)
                .queryParam("per_page","100")
                .build()
                .encode()
                .toUri();
        List<Repository> fetchedRepos = new ArrayList<>();

        String receivedReposString = "";

        try {
            receivedReposString = restTemplate.getForObject(targetUrl, String.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
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
            fetchedRepos.add(parseRepository(repoJson, userTimeZoneOffset));
        }
        return fetchedRepos;
    }

    private Repository parseRepository(JsonNode json, int userTimeZoneOffset) throws ParseToJsonNotPossible {
        Repository repo = new Repository();

        if (json.has(RESP_FIELD_FULL_NAME)) {
            repo.setName(json.get(RESP_FIELD_FULL_NAME).asText());
        }
        else
            throw new ParseToJsonNotPossible();
        if (json.has(RESP_FIELD_DESCRIPTION) && !json.get(RESP_FIELD_DESCRIPTION).isNull()) {
            repo.setDescription(json.get(RESP_FIELD_DESCRIPTION).asText());
        }
        if (json.has(RESP_FIELD_URL) && !json.get(RESP_FIELD_URL).isNull()) {
            repo.setUrl(json.get(RESP_FIELD_URL).asText());
        }
        if (json.has(RESP_FIELD_CREATED_AT) && !json.get(RESP_FIELD_CREATED_AT).isNull()) {
            repo.setCreatedAt(LocalDateTime.ofInstant(Instant.parse(json.get(RESP_FIELD_CREATED_AT).asText()), ZoneOffset.ofHours(userTimeZoneOffset/ MINUTES_IN_HOUR)));
        }
        return repo;
    }
}
