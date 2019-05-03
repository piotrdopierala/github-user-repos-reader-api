package pl.dopierala.allegroreporeaderapi;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import pl.dopierala.allegroreporeaderapi.Model.Repository;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static pl.dopierala.allegroreporeaderapi.ServiceTest.createSampleRepos;


@SpringBootTest
@RunWith(SpringRunner.class)
public class RepoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private RepoService repoServiceMock;

    @Before
    public void prepareTest(){
        MockitoAnnotations.initMocks(this);
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void context_load(){
        Assert.assertNotNull(repoServiceMock);
    }

    @Test
    public void should_ask_service_for_sample_user_repositories() throws Exception {

        List<Repository> sampleRepos = createSampleRepos();

        when(repoServiceMock.getUserRepos("sample_user", 0)).thenReturn(sampleRepos);

        mockMvc.perform(get("/api/v1/getRepos/sample_user").accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$..name").value(Matchers.hasItem("sample_repo_1")))
                .andExpect(jsonPath("$..name").value(Matchers.hasItem("sample_repo_2")))
                .andExpect(status().isOk());
    }
}