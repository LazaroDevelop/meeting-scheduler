package net.space.developer.meetingscheduler.config;

import net.space.developer.meetingscheduler.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ObservabilityAndDocsIntegrationTest {

    private static final RequestPostProcessor ADMIN = httpBasic("admin", "admin123");

    @Autowired
    private MockMvc mvc;

    @Test
    void healthIsPublicAndUp() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void exposesKubernetesStyleProbes() throws Exception {
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
        mvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk());
    }

    @Test
    void exposesCacheMetricsToAuthenticatedUsers() throws Exception {
        mvc.perform(get("/actuator/metrics").with(ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names", hasItem("cache.gets")));
    }

    @Test
    void exposesCachesEndpoint() throws Exception {
        mvc.perform(get("/actuator/caches").with(ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cacheManagers.cacheManager.caches.bookingSearch").exists());
    }

    @Test
    void exposesBuildInfo() throws Exception {
        mvc.perform(get("/actuator/info").with(ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.build.artifact").value("meeting-scheduler"));
    }

    @Test
    void publishesOpenApiDocumentWithBasicAuthScheme() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Meeting Scheduler API"))
                .andExpect(jsonPath("$.paths['/api/v1/rooms']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/bookings/{id}'].delete.security").exists())
                .andExpect(jsonPath("$.paths['/api/v1/bookings/{id}'].get.security").doesNotExist())
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth.scheme").value("basic"));
    }

    @Test
    void servesSwaggerUi() throws Exception {
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
