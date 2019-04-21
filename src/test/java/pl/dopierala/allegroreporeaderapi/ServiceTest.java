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
import pl.dopierala.allegroreporeaderapi.Model.Repository;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void givenMockRestService_whenGetIsCalled_thenReturnsMockedObject() throws JsonProcessingException {

        List<RepositoryMockModel> mockRepos = new ArrayList<>();

        RepositoryMockModel rep1 = new RepositoryMockModel();
        rep1.setName("mock_repo1");
        rep1.setCreatedAt(LocalDateTime.now());
        rep1.setUrl("http://localhost");
        rep1.setDescription("Mock repo 1 description");

        RepositoryMockModel rep2 = new RepositoryMockModel();
        rep2.setName("mock_repo2");
        rep2.setCreatedAt(LocalDateTime.now());
        rep2.setUrl("http://localhost");
        rep2.setDescription("Mock repo 2 description");

        mockRepos.add(rep1);
        mockRepos.add(rep2);

        String test = mapper.writeValueAsString(mockRepos);

        mockServer.expect(ExpectedCount.once(),
                requestTo("https://api.github.com/users/mockUser/repos"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(mapper.writeValueAsString(mockRepos))
                );

        List<Repository> fetchedRepos = repoService.getUserRepos("mockUser");
        mockServer.verify();

        Assert.assertEquals(mockRepos,fetchedRepos);

    }

    @Test
    public void null_name_should_return_empty_list() {
        List<Repository> retList = repoService.getUserRepos(null);
        assertThat(retList, hasSize(0));
    }

    @Test
    public void invalid_Json_should_throw_exception() throws Exception {

    }

}
