package rocks.artur;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RestServiceTest {

    private ClientAndServer mockFitsServer;

    @LocalServerPort
    private int port;
    private final int MOCK_FITS_SERVER_PORT = 8888;

    public static String VALID_FITS_RESULT = """
            <?xml version="1.0" encoding="UTF-8"?>
            <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://hul.harvard.edu/ois/xml/ns/fits/fits_output http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd" version="1.5.0" timestamp="2/19/20 1:26 PM">
              <identification status="CONFLICT">
                <identity format="Markdown" mimetype="text/markdown" toolname="FITS" toolversion="1.5.0">
                  <tool toolname="Droid" toolversion="6.4" />
                  <externalIdentifier toolname="Droid" toolversion="6.4" type="puid">fmt/1149</externalIdentifier>
                </identity>
                <identity format="Plain text" mimetype="text/plain" toolname="FITS" toolversion="1.5.0">
                  <tool toolname="Jhove" toolversion="1.20.1" />
                  <tool toolname="file utility" toolversion="5.35" />
                </identity>
              </identification>
              <fileinfo>
                <size toolname="Jhove" toolversion="1.20.1">903</size>
                <filepath toolname="OIS File Information" toolversion="1.0" status="SINGLE_RESULT">/usr/local/tomcat/webapps/fits/upload/1582118786085/README.md</filepath>
                <filename toolname="OIS File Information" toolversion="1.0" status="SINGLE_RESULT">README.md</filename>
                <md5checksum toolname="OIS File Information" toolversion="1.0" status="SINGLE_RESULT">133c6cf05a139fa2e472ce6fa11bb5d2</md5checksum>
                <fslastmodified toolname="OIS File Information" toolversion="1.0" status="SINGLE_RESULT">1582118786000</fslastmodified>
              </fileinfo>
              <filestatus />
              <metadata />
              <statistics fitsExecutionTime="178">
                <tool toolname="MediaInfo" toolversion="0.7.75" status="did not run" />
                <tool toolname="OIS Audio Information" toolversion="0.1" status="did not run" />
                <tool toolname="ADL Tool" toolversion="0.1" status="did not run" />
                <tool toolname="VTT Tool" toolversion="0.1" status="did not run" />
                <tool toolname="Droid" toolversion="6.4" executionTime="10" />
                <tool toolname="Jhove" toolversion="1.20.1" executionTime="42" />
                <tool toolname="file utility" toolversion="5.35" executionTime="65" />
                <tool toolname="Exiftool" toolversion="11.54" executionTime="176" />
                <tool toolname="NLNZ Metadata Extractor" toolversion="3.6GA" status="did not run" />
                <tool toolname="OIS File Information" toolversion="1.0" executionTime="5" />
                <tool toolname="OIS XML Metadata" toolversion="0.2" status="did not run" />
                <tool toolname="ffident" toolversion="0.2" executionTime="14" />
                <tool toolname="Tika" toolversion="1.21" executionTime="69" />
              </statistics>
            </fits>


            """;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
    @BeforeEach
    void setUp() {
        mockFitsServer = ClientAndServer.startClientAndServer(MOCK_FITS_SERVER_PORT);
    }

    @AfterEach
    public void stopServer() {
        mockFitsServer.stop();
    }

    @Test
    void emptyTest() {
        String str = given().port(port)
                .when().get("/")
                .then()
                .statusCode(200).extract().asString();
        System.out.println("Result: " + str);

    }

    @Test
    void getCollectionStatisticsTest() {
        String str = given().port(port)
                .when().post("/statistics")
                .then()
                .statusCode(200).extract().asString();
        System.out.println("Result: " + str);

    }

    @Test
    void getPropertiesTest() {
        String str = given().port(port)
                .when().get("/properties")
                .then()
                .statusCode(200).extract().asString();
        System.out.println("Result: " + str);
    }


    @Test
    void getSourcesTest() {
        String str = given().port(port)
                .when().get("/sources")
                .then()
                .statusCode(200).extract().asString();
        System.out.println("Result: " + str);
    }
    @Test
    void getOperatorsTest() {
        String str = given().port(port)
                .when().get("/operators")
                .then()
                .statusCode(200).extract().asString();
        System.out.println("Result: " + str);
    }

    @Test
    void getObjectTest() {
        String str = given().port(port).param("filepath","/home/artur/file1")
                .when().post("/object")
                .then()
                .statusCode(200).extract().asString();
        System.out.println("Result: " + str);
    }

    @Test
    void getObjectsTest() {
        String str = given().port(port).param("filter", " format='docx' OR format='pdf'")
                .when().post("/objects")
                .then()
                .statusCode(200).extract().asString();
        System.out.println("Result: " + str);
    }

    @Test
    void getObjectConflictsTest() {
        String str = given().port(port).param("filepath","/home/artur/file1")
                .when().post("/objectconflicts")
                .then()
                .statusCode(200).extract().asString();
        System.out.println("Result: " + str);
    }


    @Test
    void getPropertyDistributionWithFilterTest() {
        String str = given().port(port)
                .param("filter", "FORMAT=\"Portable Document Format\"")
                .param("property", "FORMAT")
                .when().post("/propertyvalues")
                .then()
                .statusCode(200).extract().asString();
        System.out.println("Result: " + str);
    }

    @Test
    void getPropertyDistributionWithoutFilterTest() {
        String str = given().port(port)
                .param("property", "FORMAT")
                .when().post("/propertyvalues")
                .then()
                .statusCode(200).extract().asString();
        System.out.println("Result: " + str);
    }

    @Test
    void uploadFileTest() {

        //First, I mock up the FITS Endpoint which produces a FITS XML.

        mockFitsServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/fits/examine")
                        .withHeader("\"Content-type\", \"application/json\""))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(VALID_FITS_RESULT)
                );


        URL resource = getClass().getClassLoader().getResource("README.md");
        File file = null;
        if (resource != null) {
            file = new File(resource.getPath());
        }


        //Then, I call my /upload endpoint, where a FITS XML is generated and the char results uploaded into DB

        given().port(port).multiPart("file",file)
                .when().post("/upload")
                .then()
                .statusCode(200).extract().asString();


        //Finally, I query the DB and return the characterisation results.

        String str =

                given().port(port)
                .param("filepath", "/usr/local/tomcat/webapps/fits/upload/1582118786085/README.md")
                .when().post("/object")
                .then()
                .statusCode(200).extract().asString();
        System.out.println("Result: " + str);

        Assert.assertNotNull(str);
    }

    @Test
    void resolveConflictsTest() {
        String str = given().port(port)
                .when().post("/resolveconflicts")
                .then()
                .statusCode(200).extract().asString();
        System.out.println("Result: " + str);
    }
}