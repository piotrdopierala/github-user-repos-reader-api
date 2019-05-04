package pl.dopierala.allegroreporeaderapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.dopierala.allegroreporeaderapi.exceptions.ParseToJsonNotPossibleException;
import pl.dopierala.allegroreporeaderapi.exceptions.UserNotFoundException;
import pl.dopierala.allegroreporeaderapi.model.Repository;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RepoService implements IRepoService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String GITHUB_API_URL = "https://api.github.com/";
    private static final String GITHUB_API_USER_REPOS = "users/%s/repos";

    private static final String RESP_FIELD_FULL_NAME = "full_name";
    private static final String RESP_FIELD_URL = "html_url";
    private static final String RESP_FIELD_DESCRIPTION = "description";
    private static final String RESP_FIELD_CREATED_AT = "created_at";

    public static final int MINUTES_IN_HOUR = 60;

    @Override
    public List<Repository> findRepositoriesByUserName(String userName, int userTimeZoneOffset) {
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
                throw new UserNotFoundException();
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode fetchedReposJson = null;
        try {
            fetchedReposJson = mapper.readTree(receivedReposString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < fetchedReposJson.size(); i++) {
            JsonNode repoJson = fetchedReposJson.get(i);
            fetchedRepos.add(parseJsonToRepository(repoJson, userTimeZoneOffset));
        }
        return fetchedRepos;
    }

    private Repository parseJsonToRepository(JsonNode json, int userTimeZoneOffset) throws ParseToJsonNotPossibleException {
        Repository repo = new Repository();

        if (json.has(RESP_FIELD_FULL_NAME)) {
            repo.setName(json.get(RESP_FIELD_FULL_NAME).asText());
        }
        else
            throw new ParseToJsonNotPossibleException();
        if (json.has(RESP_FIELD_DESCRIPTION) && !json.get(RESP_FIELD_DESCRIPTION).isNull()) {
            repo.setDescription(json.get(RESP_FIELD_DESCRIPTION).asText());
        }
        if (json.has(RESP_FIELD_URL) && !json.get(RESP_FIELD_URL).isNull()) {
            repo.setUrl(json.get(RESP_FIELD_URL).asText());
        }
        if (json.has(RESP_FIELD_CREATED_AT) && !json.get(RESP_FIELD_CREATED_AT).isNull()) {
            repo.setCreatedAt(calculateDateAtUserTimeZone(json.get(RESP_FIELD_CREATED_AT).asText(), userTimeZoneOffset));
        }
        return repo;
    }

    private LocalDateTime calculateDateAtUserTimeZone(String dateToCalculate, int userTimeZoneOffset) {
        Instant instant = Instant.parse(dateToCalculate);
        return LocalDateTime.ofInstant(instant, ZoneOffset.ofHours(userTimeZoneOffset/ MINUTES_IN_HOUR));
    }
}
