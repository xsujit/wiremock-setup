import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import wiremock.org.apache.http.client.methods.CloseableHttpResponse;
import wiremock.org.apache.http.client.methods.HttpGet;
import wiremock.org.apache.http.client.methods.HttpPost;
import wiremock.org.apache.http.impl.client.CloseableHttpClient;
import wiremock.org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class TestCases {

    private CloseableHttpClient httpClient;
    private CloseableHttpResponse response;
    private static final String BASE_URL = "http://localhost:8090/";

    @BeforeTest
    public void setup() {
        httpClient = HttpClients.createDefault();
    }

    @Test
    public void sd101ShouldReturn200() throws IOException {

        response = httpClient.execute(new HttpGet(BASE_URL + "serviceDelivery/101"));
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
    }

    @Test
    public void sd102ShouldReturn200() throws IOException {

        response = httpClient.execute(new HttpGet(BASE_URL + "serviceDelivery/102"));
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
    }

    @Test
    public void sd104shouldReturn404() throws IOException {

        response = httpClient.execute(new HttpGet(BASE_URL + "serviceDelivery/104"));
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 404);
    }

    @Test
    public void postShouldReturn200() throws IOException {

        response = httpClient.execute(new HttpPost(BASE_URL + "serviceDelivery"));
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
    }

    @AfterMethod
    public void closeResponse() throws IOException {
        response.close();
    }

    @AfterTest
    public void closeHttpClient() throws IOException {
        httpClient.close();
    }

}
