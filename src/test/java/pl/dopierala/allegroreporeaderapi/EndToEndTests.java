package pl.dopierala.allegroreporeaderapi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.junit.Assert.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class EndToEndTests {

    private WebDriver webDriver;
    private WebDriverWait webDriverWait;

    @Autowired
    RestTemplate restTemplate;

    @Before
    public void setup(){
        System.setProperty("webdriver.chrome.driver","selenium/chromedriver.exe");
        webDriver = new ChromeDriver();
        webDriverWait = new WebDriverWait(webDriver,10);
    }

    @After
    public void teardown(){
        if(Objects.nonNull(webDriver)){
            webDriver.quit();
        }
    }

    @Test
    public void should_load_page_successfully(){
        webDriver.get("http://localhost:4200");
        String pageSource = webDriver.getPageSource();
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("gihubUserNameInput")));
        assertTrue(pageSource.contains("RepoReaderClient"));
    }

    @Test
    public void shuld_load_real_repositoryies(){

        webDriver.get("http://localhost:4200");
        webDriver.findElement(By.id("gihubUserNameInput")).sendKeys("piotrdopierala");
        webDriver.findElement(By.id("showReposButton")).click();
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("repositoriesList")));
        String pageSource = webDriver.getPageSource();
        assertTrue(pageSource.contains("piotrdopierala/TetrisGame"));
        assertTrue(pageSource.contains("created at"));
    }

    @Test
    public void should_generate_alert_wrong_GithubUserName(){



        webDriver.get("http://localhost:4200");
        webDriver.findElement(By.id("gihubUserNameInput")).sendKeys("NONEXISTENTUSER");//TODO add mockMvc and return 404 in case test (non existent) user creates an account
        webDriver.findElement(By.id("showReposButton")).click();
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("alert")));
        String pageSource = webDriver.getPageSource();
        assertTrue(pageSource.contains("Something went wrong. Check user name and try again."));
    }
}
