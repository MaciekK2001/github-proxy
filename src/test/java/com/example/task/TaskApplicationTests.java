package com.example.task;

import com.example.task.Model.RepositoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = {
                "github.api.base-url=${wiremock.server.baseUrl}"
        }
)
@EnableWireMock
class TaskApplicationTests {

    @LocalServerPort
    int port;

    RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldReturnOnlyNonForkRepositoriesWithBranchesAndLastCommitSha() {
        stubFor(get(urlEqualTo("/users/octocat/repos"))
                .willReturn(okJson("""
                        [
                          { "name": "repo1", "fork": false, "owner": { "login": "octocat" } },
                          { "name": "repo2", "fork": true,  "owner": { "login": "octocat" } }
                        ]
                        """)));

        stubFor(get(urlEqualTo("/repos/octocat/repo1/branches"))
                .willReturn(okJson("""
                        [
                          { "name": "main", "commit": { "sha": "abc123" } }
                        ]
                        """)));

        var response = restClient.get()
                .uri("/users/octocat/repositories")
                .retrieve()
                .body(RepositoryResponse[].class);

        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response[0].repositoryName()).isEqualTo("repo1");
        assertThat(response[0].ownerLogin()).isEqualTo("octocat");
        assertThat(response[0].branches()).hasSize(1);
        assertThat(response[0].branches().getFirst().lastCommitSha()).isEqualTo("abc123");

        verify(0, getRequestedFor(urlEqualTo("/repos/octocat/repo2/branches")));
    }

    @Test
    void shouldReturn404InRequiredFormatWhenGithubUserDoesNotExist() {
        stubFor(get(urlEqualTo("/users/no_such_user/repos"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                { "message": "Not Found" }
                                """)));

        var ex = catchThrowable(() -> restClient.get()
                .uri("/users/no_such_user/repositories")
                .retrieve()
                .body(String.class));

        assertThat(ex).isInstanceOf(HttpClientErrorException.NotFound.class);

        var notFound = (HttpClientErrorException.NotFound) ex;
        assertThat(notFound.getStatusCode().value()).isEqualTo(404);

        String body = notFound.getResponseBodyAsString();
        assertThat(body).contains("\"status\":404");
        assertThat(body).contains("\"message\"");
    }
}
