package com.vsp.api.audit.service.junit;

import com.vsp.api.audit.service.AuditServiceWinkClient;
import com.vsp.api.product.model.audit.AuditMessage;
import com.vsp.api.product.model.audit.AuditMessages;
import com.vsp.api.audit.service.Application;
import org.apache.wink.client.ClientResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class})
@ActiveProfiles("embedded")
public class AuditServiceWinkClientTest {

    @Autowired
    private AuditServiceWinkClient auditClient;

    private String clientId = "99999999";
    private String classId = "1111";
    private String classId2 = "2222";
    private String effBegin = "2018-05-01";

    @BeforeClass
    public static void setup() {
//        auditClient = new AuditServiceWinkClient();
        System.out.println("****** Begin of Audit Test ******");
    }

    @AfterClass
    public static void tearDown() {
        System.out.println("****** End of Audit Test ******");
    }


    private static AuditMessage buildAuditMessage(String desc, String user, String... actions) {
        AuditMessage message = new AuditMessage();
        message.setDecription(desc);
        message.setUser(user);
        message.setEdits(Arrays.asList(actions));
        return message;
    }

    @Test
    public void testGetToken() {
        String accessToken = auditClient.getToken();

        assert (accessToken != null && accessToken.length() > 0);

        System.out.println("testGetToken: accessToken=" + accessToken);
    }

    @Test
    public void testRetrieveAuditMessages() {

        ClientResponse response = auditClient.retrieveAudit(clientId, classId, effBegin);

        assert (response != null && response.getMessage().equals("OK"));

        System.out.println("testRetrieveAuditMessages: Response Message=" + response.getMessage());
        System.out.println("Result=" + response.getEntity(String.class));
    }

    @Test
    public void testApproveAuditMessages() {

        ClientResponse response = auditClient.approveAudit(clientId, classId, effBegin);

        assert (response != null && response.getMessage().equals("OK"));

        System.out.println("testApproveAuditMessages: Response Message=" + response.getMessage());
        System.out.println("Result=" + response.getEntity(String.class));
    }

    @Test
    public void testAddAuditMessages() {

        AuditMessages auditMessages = new AuditMessages();
        auditMessages.setClientId(clientId);
        auditMessages.setClassId(classId);
        auditMessages.setEffBegin(effBegin);
        auditMessages.setMessages(new ArrayList<AuditMessage>());
        auditMessages.getMessages().add(buildAuditMessage("Test Description", "junit1", "Test Action"));

        ClientResponse response = auditClient.saveAudit(auditMessages);

        assert (response != null && response.getMessage().equals("OK"));

        System.out.println("testAddAuditMessages: Response Message=" + response.getMessage());
        System.out.println("Result=" + response.getEntity(String.class));
    }

}
