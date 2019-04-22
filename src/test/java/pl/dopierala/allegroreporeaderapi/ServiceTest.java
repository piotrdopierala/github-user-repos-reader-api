package pl.dopierala.allegroreporeaderapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import pl.dopierala.allegroreporeaderapi.Exceptions.ParseToJsonNotPossible;
import pl.dopierala.allegroreporeaderapi.Exceptions.UserNotFound;
import pl.dopierala.allegroreporeaderapi.Model.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ServiceTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    RepoService repoService;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();
    private List<Repository> repositoriesSample;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        repositoriesSample = createSampleRepos();
    }

    @Test
    public void givenMockRestService_whenGetIsCalled_thenReturnsMockedObject() throws JsonProcessingException {

        String reposMockJson = generateJsonStringFromRepos(repositoriesSample);

        mockServer.expect(ExpectedCount.once(),
                requestTo("https://api.github.com/users/mockUser/repos"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(reposMockJson)
                );

        List<Repository> fetchedRepos = repoService.getUserRepos("mockUser");
        mockServer.verify();

        Assert.assertEquals(repositoriesSample, fetchedRepos);
    }

    @Test
    public void null_name_should_return_empty_list() {
        List<Repository> retList = repoService.getUserRepos(null);
        assertThat(retList, hasSize(0));
    }

    @Test(expected = UserNotFound.class)
    public void wrong_name_should_throw_exception() {
        mockServer.expect(ExpectedCount.once(),
                requestTo("https://api.github.com/users/non_exist_user/repos"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                );

        List<Repository> retList = repoService.getUserRepos("non_exist_user");
        mockServer.verify();
    }

    @Test(expected = ParseToJsonNotPossible.class)
    public void invalid_Json_should_throw_exception(){
        mockServer.expect(ExpectedCount.once(),
                requestTo("https://api.github.com/users/non_exist_user/repos"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                .body("[{\"test\":\"value\"},{\"test2\":2}]")
                );

        List<Repository> retList = repoService.getUserRepos("non_exist_user");
        mockServer.verify();
    }

    private String generateJsonStringFromRepos(List<Repository> repos) throws JsonProcessingException {
        List<Map<String, Object>> repositoriesJsonMockModel = new ArrayList<>();
        for (Repository repo : repos) {
            Map<String, Object> repositoryJsonMockModel = new HashMap<>();
            repositoryJsonMockModel.put("full_name", repo.getName());
            repositoryJsonMockModel.put("html_url", repo.getUrl());
            repositoryJsonMockModel.put("description", repo.getDescription());
            repositoryJsonMockModel.put("created_at", repo.getCreatedAt().atZone(ZoneId.of("Europe/Warsaw")).withZoneSameInstant(ZoneId.of("Z")).toInstant().toString());
            repositoriesJsonMockModel.add(repositoryJsonMockModel);
        }
        return mapper.writeValueAsString(repositoriesJsonMockModel);
    }

    public static List<Repository> createSampleRepos() {
        List<Repository> repos = new ArrayList<>();

        Repository repo1 = new Repository();
        repo1.setName("sample_repo_1");
        repo1.setUrl("http://localhost/users/sample_user/repos/repo1");
        repo1.setDescription("sample repo 1 test description");
        repo1.setCreatedAt(LocalDateTime.of(2017, 05, 21, 9, 45));

        Repository repo2 = new Repository();
        repo2.setName("sample_repo_2");
        repo2.setUrl("http://localhost/users/sample_user/repos/repo2");
        repo2.setDescription("sample repo 2 test description");
        repo2.setCreatedAt(LocalDateTime.now());

        repos.add(repo1);
        repos.add(repo2);

        return repos;
    }

}
