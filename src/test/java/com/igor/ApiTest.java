package com.igor;

import com.jayway.restassured.RestAssured;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import static org.hamcrest.CoreMatchers.is;

public class ApiTest {

  private static DockerComposeContainer composeContainer = new DockerComposeContainer(new File("docker-compose.yml"))
      .withLocalCompose(true)
      .withPull(false)
      .withExposedService("application_1", 8080, Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(3)));

  static {
    composeContainer.start();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> composeContainer.stop()));
  }

  @Before
  public void setup() throws InterruptedException {
    Thread.sleep(5000);

    String appHost = composeContainer.getServiceHost("application_1", 8080);
    Integer appPort = composeContainer.getServicePort("application_1", 8080);
    RestAssured.baseURI = createUrl(appHost, appPort);
  }

  private static String createUrl(String host, Integer port) {
    return String.format("http://%s:%d", host, port);
  }

  @Test
  public void test() throws Exception {
    RestAssured.given()
        .when()
        .get("/api/test")
        .then()
        .statusCode(HttpStatus.SC_OK)
        .assertThat()
        .body(is("Hello World"));

    dumpJacocoCoverage();
  }

  private void dumpJacocoCoverage() throws Exception {
    JMXServiceURL jmxURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");
    try (JMXConnector jmxc = JMXConnectorFactory.connect(jmxURL)) {
      MBeanServerConnection connection = jmxc.getMBeanServerConnection();
      ObjectName objName = new ObjectName("org.jacoco:type=Runtime");
      connection.invoke(objName, "dump",
          new Object[]{true},
          new String[]{Boolean.TYPE.getName()}
      );
    }
  }
}

