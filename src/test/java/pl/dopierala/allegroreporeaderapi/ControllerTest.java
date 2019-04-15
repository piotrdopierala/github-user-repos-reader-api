package pl.dopierala.allegroreporeaderapi;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private Controller controller;


    @Before
    public void prepareTest(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                    .standaloneSetup(controller)
                    .build();
    }

    @Test
    public void context_load(){
        Assert.assertNotNull(controller);
    }

    @Test
    public void should_return_repositories() throws Exception {
        mockMvc.perform(get("/api/v1/getRepos/piotrdopierala").accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$..name").value(Matchers.hasItem("piotrdopierala/CodeWars")))
                .andExpect(jsonPath("$..name").value(Matchers.hasItem("piotrdopierala/MedivalGame")))
                .andExpect(status().isOk());
    }
}