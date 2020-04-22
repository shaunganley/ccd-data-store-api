package uk.gov.hmcts.ccd.v2.external.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.ccd.MockUtils;
import uk.gov.hmcts.ccd.WireMockBaseTest;
import uk.gov.hmcts.ccd.auditlog.AuditRepository;
import uk.gov.hmcts.ccd.v2.V2;
import uk.gov.hmcts.ccd.v2.external.resource.StartTriggerResource;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StartTriggerControllerIT extends WireMockBaseTest {
    private static final String REQUEST_ID = "request-id";
    private static final String REQUEST_ID_VALUE = "1234567898765432";

    @Inject
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @SpyBean
    private AuditRepository auditRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        doReturn(authentication).when(securityContext).getAuthentication();
        SecurityContextHolder.setContext(securityContext);

        MockUtils.setSecurityAuthorities(authentication, MockUtils.ROLE_CASEWORKER_PUBLIC);

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void getStartCaseTrigger_should_return_200() throws Exception {
        final String URL =  "/case-types/TestAddressBookCreatorCase/event-triggers/NO_PRE_STATES_EVENT";

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, "Bearer user1");
        headers.add(V2.EXPERIMENTAL_HEADER, "true");
        headers.add(REQUEST_ID, REQUEST_ID_VALUE);

        final MvcResult result = mockMvc.perform(get(URL)
            .contentType(JSON_CONTENT_TYPE)
            .accept(V2.MediaType.START_CASE_TRIGGER)
            .headers(headers))
            .andExpect(status().is(200))
            .andReturn();

        final StartTriggerResource startTriggerResource = mapper.readValue(result.getResponse().getContentAsString(), StartTriggerResource.class);
        assertNotNull("UI Start Trigger Resource is null", startTriggerResource);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:sql/insert_cases.sql"})
    public void getStartEventTrigger_should_return_200() throws Exception {
        String caseId = "1504259907353529";
        String triggerId = "HAS_PRE_STATES_EVENT";
        final String URL =  "/cases/" + caseId + "/event-triggers/" + triggerId;

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, "Bearer user1");
        headers.add(V2.EXPERIMENTAL_HEADER, "true");
        headers.add(REQUEST_ID, REQUEST_ID_VALUE);

        final MvcResult result = mockMvc.perform(get(URL)
            .contentType(JSON_CONTENT_TYPE)
            .accept(V2.MediaType.START_EVENT_TRIGGER)
            .headers(headers))
            .andExpect(status().is(200))
            .andReturn();

        final StartTriggerResource startTriggerResource = mapper.readValue(result.getResponse().getContentAsString(), StartTriggerResource.class);
        assertNotNull("UI Start Trigger Resource is null", startTriggerResource);
    }
}
