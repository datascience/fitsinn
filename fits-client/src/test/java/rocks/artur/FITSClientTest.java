package rocks.artur;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.test.context.ActiveProfiles;
import rocks.artur.FITSClient.FITSClient;
import rocks.artur.domain.CharacterisationResult;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class FITSClientTest {
    private ClientAndServer mockServer;

    private int MOCK_SERVER_PORT = 8888;

    public static String VALID_FITS_RESULT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd\" version=\"1.5.0\" timestamp=\"2/19/20 1:26 PM\">\n" +
            "  <identification status=\"CONFLICT\">\n" +
            "    <identity format=\"Markdown\" mimetype=\"text/markdown\" toolname=\"FITS\" toolversion=\"1.5.0\">\n" +
            "      <tool toolname=\"Droid\" toolversion=\"6.4\" />\n" +
            "      <externalIdentifier toolname=\"Droid\" toolversion=\"6.4\" type=\"puid\">fmt/1149</externalIdentifier>\n" +
            "    </identity>\n" +
            "    <identity format=\"Plain text\" mimetype=\"text/plain\" toolname=\"FITS\" toolversion=\"1.5.0\">\n" +
            "      <tool toolname=\"Jhove\" toolversion=\"1.20.1\" />\n" +
            "      <tool toolname=\"file utility\" toolversion=\"5.35\" />\n" +
            "    </identity>\n" +
            "  </identification>\n" +
            "  <fileinfo>\n" +
            "    <size toolname=\"Jhove\" toolversion=\"1.20.1\">903</size>\n" +
            "    <filepath toolname=\"OIS File Information\" toolversion=\"1.0\" status=\"SINGLE_RESULT\">/usr/local/tomcat/webapps/fits/upload/1582118786085/README.md</filepath>\n" +
            "    <filename toolname=\"OIS File Information\" toolversion=\"1.0\" status=\"SINGLE_RESULT\">README.md</filename>\n" +
            "    <md5checksum toolname=\"OIS File Information\" toolversion=\"1.0\" status=\"SINGLE_RESULT\">133c6cf05a139fa2e472ce6fa11bb5d2</md5checksum>\n" +
            "    <fslastmodified toolname=\"OIS File Information\" toolversion=\"1.0\" status=\"SINGLE_RESULT\">1582118786000</fslastmodified>\n" +
            "  </fileinfo>\n" +
            "  <filestatus />\n" +
            "  <metadata />\n" +
            "  <statistics fitsExecutionTime=\"178\">\n" +
            "    <tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"did not run\" />\n" +
            "    <tool toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"did not run\" />\n" +
            "    <tool toolname=\"ADL Tool\" toolversion=\"0.1\" status=\"did not run\" />\n" +
            "    <tool toolname=\"VTT Tool\" toolversion=\"0.1\" status=\"did not run\" />\n" +
            "    <tool toolname=\"Droid\" toolversion=\"6.4\" executionTime=\"10\" />\n" +
            "    <tool toolname=\"Jhove\" toolversion=\"1.20.1\" executionTime=\"42\" />\n" +
            "    <tool toolname=\"file utility\" toolversion=\"5.35\" executionTime=\"65\" />\n" +
            "    <tool toolname=\"Exiftool\" toolversion=\"11.54\" executionTime=\"176\" />\n" +
            "    <tool toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.6GA\" status=\"did not run\" />\n" +
            "    <tool toolname=\"OIS File Information\" toolversion=\"1.0\" executionTime=\"5\" />\n" +
            "    <tool toolname=\"OIS XML Metadata\" toolversion=\"0.2\" status=\"did not run\" />\n" +
            "    <tool toolname=\"ffident\" toolversion=\"0.2\" executionTime=\"14\" />\n" +
            "    <tool toolname=\"Tika\" toolversion=\"1.21\" executionTime=\"69\" />\n" +
            "  </statistics>\n" +
            "</fits>\n" +
            "\n" +
            "\n";


    public static String VALID_FITS_RESULT2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd\" version=\"1.5.0\" timestamp=\"7/29/20 7:50 PM\">\n" +
            "  <identification>\n" +
            "    <identity format=\"Portable Network Graphics\" mimetype=\"image/png\" toolname=\"FITS\" toolversion=\"1.5.0\">\n" +
            "      <tool toolname=\"Droid\" toolversion=\"6.4\" />\n" +
            "      <tool toolname=\"Exiftool\" toolversion=\"11.54\" />\n" +
            "      <tool toolname=\"ffident\" toolversion=\"0.2\" />\n" +
            "      <tool toolname=\"Tika\" toolversion=\"1.21\" />\n" +
            "      <version toolname=\"Droid\" toolversion=\"6.4\">1.0</version>\n" +
            "      <externalIdentifier toolname=\"Droid\" toolversion=\"6.4\" type=\"puid\">fmt/11</externalIdentifier>\n" +
            "    </identity>\n" +
            "  </identification>\n" +
            "  <fileinfo>\n" +
            "    <filepath toolname=\"OIS File Information\" toolversion=\"1.0\" status=\"SINGLE_RESULT\">/usr/local/tomcat/webapps/fits/upload/1596052237783/main</filepath>\n" +
            "    <filename toolname=\"OIS File Information\" toolversion=\"1.0\" status=\"SINGLE_RESULT\">main</filename>\n" +
            "    <size toolname=\"OIS File Information\" toolversion=\"1.0\" status=\"SINGLE_RESULT\">1875256</size>\n" +
            "    <md5checksum toolname=\"OIS File Information\" toolversion=\"1.0\" status=\"SINGLE_RESULT\">926a7c8c079e4ccb837410746b2919e2</md5checksum>\n" +
            "    <fslastmodified toolname=\"OIS File Information\" toolversion=\"1.0\" status=\"SINGLE_RESULT\">1596052237000</fslastmodified>\n" +
            "  </fileinfo>\n" +
            "  <filestatus />\n" +
            "  <metadata>\n" +
            "    <image>\n" +
            "      <compressionScheme toolname=\"Exiftool\" toolversion=\"11.54\" status=\"CONFLICT\">Deflate/Inflate</compressionScheme>\n" +
            "      <compressionScheme toolname=\"Tika\" toolversion=\"1.21\" status=\"CONFLICT\">Deflate</compressionScheme>\n" +
            "      <imageWidth toolname=\"Exiftool\" toolversion=\"11.54\">2400</imageWidth>\n" +
            "      <imageHeight toolname=\"Exiftool\" toolversion=\"11.54\">1531</imageHeight>\n" +
            "      <orientation toolname=\"Tika\" toolversion=\"1.21\" status=\"SINGLE_RESULT\">normal*</orientation>\n" +
            "      <standard>\n" +
            "        <mix:mix xmlns:mix=\"http://www.loc.gov/mix/v20\">\n" +
            "          <mix:BasicDigitalObjectInformation>\n" +
            "            <mix:Compression>\n" +
            "              <mix:compressionScheme>Deflate/Inflate</mix:compressionScheme>\n" +
            "            </mix:Compression>\n" +
            "          </mix:BasicDigitalObjectInformation>\n" +
            "          <mix:BasicImageInformation>\n" +
            "            <mix:BasicImageCharacteristics>\n" +
            "              <mix:imageWidth>2400</mix:imageWidth>\n" +
            "              <mix:imageHeight>1531</mix:imageHeight>\n" +
            "              <mix:PhotometricInterpretation />\n" +
            "            </mix:BasicImageCharacteristics>\n" +
            "          </mix:BasicImageInformation>\n" +
            "          <mix:ImageCaptureMetadata>\n" +
            "            <mix:GeneralCaptureInformation />\n" +
            "            <mix:orientation>normal*</mix:orientation>\n" +
            "          </mix:ImageCaptureMetadata>\n" +
            "          <mix:ImageAssessmentMetadata>\n" +
            "            <mix:SpatialMetrics />\n" +
            "            <mix:ImageColorEncoding />\n" +
            "          </mix:ImageAssessmentMetadata>\n" +
            "        </mix:mix>\n" +
            "      </standard>\n" +
            "    </image>\n" +
            "  </metadata>\n" +
            "  <statistics fitsExecutionTime=\"2488\">\n" +
            "    <tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"did not run\" />\n" +
            "    <tool toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"did not run\" />\n" +
            "    <tool toolname=\"ADL Tool\" toolversion=\"0.1\" status=\"did not run\" />\n" +
            "    <tool toolname=\"VTT Tool\" toolversion=\"0.1\" status=\"did not run\" />\n" +
            "    <tool toolname=\"Droid\" toolversion=\"6.4\" executionTime=\"34\" />\n" +
            "    <tool toolname=\"Jhove\" toolversion=\"1.20.1\" executionTime=\"2454\" />\n" +
            "    <tool toolname=\"file utility\" toolversion=\"5.35\" executionTime=\"136\" />\n" +
            "    <tool toolname=\"Exiftool\" toolversion=\"11.54\" executionTime=\"318\" />\n" +
            "    <tool toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.6GA\" status=\"did not run\" />\n" +
            "    <tool toolname=\"OIS File Information\" toolversion=\"1.0\" executionTime=\"33\" />\n" +
            "    <tool toolname=\"OIS XML Metadata\" toolversion=\"0.2\" status=\"did not run\" />\n" +
            "    <tool toolname=\"ffident\" toolversion=\"0.2\" executionTime=\"32\" />\n" +
            "    <tool toolname=\"Tika\" toolversion=\"1.21\" executionTime=\"46\" />\n" +
            "  </statistics>\n" +
            "</fits>\n";


    public static String VALID_FITS_RESULT3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd\" version=\"0.6.0\" timestamp=\"12/27/11 10:49 AM\">\n" +
            "  <identification>\n" +
            "    <identity format=\"Portable Document Format\" mimetype=\"application/pdf\" toolname=\"FITS\" toolversion=\"0.6.0\">\n" +
            "      <tool toolname=\"Jhove\" toolversion=\"1.5\" />\n" +
            "      <tool toolname=\"file utility\" toolversion=\"5.03\" />\n" +
            "      <tool toolname=\"Exiftool\" toolversion=\"7.74\" />\n" +
            "      <tool toolname=\"Droid\" toolversion=\"3.0\" />\n" +
            "      <tool toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.4GA\" />\n" +
            "      <tool toolname=\"ffident\" toolversion=\"0.2\" />\n" +
            "      <version toolname=\"Jhove\" toolversion=\"1.5\">1.4</version>\n" +
            "      <externalIdentifier toolname=\"Droid\" toolversion=\"3.0\" type=\"puid\">fmt/18</externalIdentifier>\n" +
            "    </identity>\n" +
            "  </identification>\n" +
            "  <fileinfo>\n" +
            "    <size toolname=\"Jhove\" toolversion=\"1.5\">39586</size>\n" +
            "    <creatingApplicationName toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.4GA\" status=\"SINGLE_RESULT\">/XPP</creatingApplicationName>\n" +
            "    <lastmodified toolname=\"Exiftool\" toolversion=\"7.74\" status=\"SINGLE_RESULT\">2011:12:27 10:44:28+01:00</lastmodified>\n" +
            "    <created toolname=\"Exiftool\" toolversion=\"7.74\" status=\"SINGLE_RESULT\">2002:04:25 13:02:24Z</created>\n" +
            "    <filepath toolname=\"OIS File Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">/home/petrov/taverna/tmp/000/000009.pdf</filepath>\n" +
            "    <filename toolname=\"OIS File Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">/home/petrov/taverna/tmp/000/000009.pdf</filename>\n" +
            "    <md5checksum toolname=\"OIS File Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">92ddc75b3b59872e6656b54b8f236764</md5checksum>\n" +
            "    <fslastmodified toolname=\"OIS File Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">1324979068000</fslastmodified>\n" +
            "  </fileinfo>\n" +
            "  <filestatus>\n" +
            "    <well-formed toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">true</well-formed>\n" +
            "    <valid toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">true</valid>\n" +
            "  </filestatus>\n" +
            "  <metadata>\n" +
            "    <document>\n" +
            "      <title toolname=\"Jhove\" toolversion=\"1.5\" status=\"CONFLICT\">Table DP-1. Profile of General Demographic Characteristics: 2000</title>\n" +
            "      <title toolname=\"Exiftool\" toolversion=\"7.74\" status=\"CONFLICT\">Census 2000 Profiles</title>\n" +
            "      <author toolname=\"Jhove\" toolversion=\"1.5\">US Census Bureau</author>\n" +
            "      <pageCount toolname=\"Jhove\" toolversion=\"1.5\">4</pageCount>\n" +
            "      <isTagged toolname=\"Jhove\" toolversion=\"1.5\">no</isTagged>\n" +
            "      <hasOutline toolname=\"Jhove\" toolversion=\"1.5\">yes</hasOutline>\n" +
            "      <hasAnnotations toolname=\"Jhove\" toolversion=\"1.5\" status=\"SINGLE_RESULT\">no</hasAnnotations>\n" +
            "      <isRightsManaged toolname=\"Exiftool\" toolversion=\"7.74\" status=\"SINGLE_RESULT\">no</isRightsManaged>\n" +
            "      <isProtected toolname=\"Exiftool\" toolversion=\"7.74\">no</isProtected>\n" +
            "      <hasForms toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.4GA\" status=\"SINGLE_RESULT\">no</hasForms>\n" +
            "    </document>\n" +
            "  </metadata>\n" +
            "</fits>";

    @BeforeEach
    void setUp() {
        mockServer = mockServer.startClientAndServer(MOCK_SERVER_PORT);
    }

    @AfterEach
    public void stopServer() {
        mockServer.stop();
    }

    @Test
    void getVersionTest() throws IOException {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/version")
                        .withHeader("\"Content-type\", \"application/json\""))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("1.5.0")
                );


        FITSClient fitsClient = new FITSClient();
        fitsClient.setFITS_URL(String.format("http://localhost:%d/", MOCK_SERVER_PORT));

        String s = fitsClient.getVersion();
        Assert.assertEquals("1.5.0", s);
    }

    @Test
    void processFileAsByteArrayTest() throws IOException {
        mockServer.when(
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
        byte[] array = Files.readAllBytes(Paths.get(resource.getPath()));

        FITSClient fitsClient = new FITSClient();
        fitsClient.setFITS_URL(String.format("http://localhost:%d", MOCK_SERVER_PORT));
        List<CharacterisationResult> output = fitsClient.processFile(array, "testFileName");

        Assert.assertEquals(12, output.size());
    }


    @Test
    void processFileTest() throws IOException {

        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/fits/examine")
                        .withHeader("\"Content-type\", \"application/json\""))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(VALID_FITS_RESULT)
                );

        FITSClient fitsClient = new FITSClient();
        fitsClient.setFITS_URL(String.format("http://localhost:%d", MOCK_SERVER_PORT));

        URL resource = getClass().getClassLoader().getResource("README.md");
        List<CharacterisationResult> output = fitsClient.processFile(new File(resource.getPath()));

        Assert.assertEquals(12, output.size());
    }

    @Test
    void processFITSFileTest() throws IOException {

        mockServer.when(
                        request()
                                .withMethod("POST")
                                .withPath("/fits/examine")
                                .withHeader("\"Content-type\", \"application/json\""))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(VALID_FITS_RESULT)
                );

        FITSClient fitsClient = new FITSClient();
        fitsClient.setFITS_URL(String.format("http://localhost:%d", MOCK_SERVER_PORT));

        URL resource = getClass().getClassLoader().getResource("998003.csv.fits.xml");
        List<CharacterisationResult> output = fitsClient.processFile(new File(resource.getPath()));

        Assert.assertEquals(17, output.size());
    }


    //The test can be run against running FITS service, i.e. fits-docker
    @Disabled
    @Test
    void processFileTestWithoutMock() throws IOException {
        FITSClient fitsClient = new FITSClient();
        fitsClient.setFITS_URL(String.format("http://localhost:%d", 8081));

        URL resource = getClass().getClassLoader().getResource("README.md");
        List<CharacterisationResult> output = fitsClient.processFile(new File(resource.getPath()));

        Assert.assertEquals(12, output.size());
    }


    //The test can be run against running FITS service, i.e. fits-docker
    @Disabled
    @Test
    void processByteArrayTestWithoutMock() throws IOException {
        FITSClient fitsClient = new FITSClient();
        fitsClient.setFITS_URL(String.format("http://localhost:%d", 8081));

        URL resource = getClass().getClassLoader().getResource("README.md");
        File file = new File(resource.getPath());
        List<CharacterisationResult> output = fitsClient.processFile(Files.readAllBytes(file.toPath()), "testFileName");

        Assert.assertEquals(9, output.size());
    }


    @Test
    void processFITSFileCSVTest() throws IOException {

        FITSClient fitsClient = new FITSClient();

        URL resource = getClass().getClassLoader().getResource("998003.csv.fits.xml");
        List<CharacterisationResult> output = fitsClient.processFile(new File(resource.getPath()));

        Assert.assertEquals(17, output.size());
    }

    @Test
    void processFITSFileHTMLTest() throws IOException {

        FITSClient fitsClient = new FITSClient();

        URL resource = getClass().getClassLoader().getResource("002526.html.fits.xml");
        List<CharacterisationResult> output = fitsClient.processFile(new File(resource.getPath()));

        Assert.assertEquals(23, output.size());
    }

    @Test
    void processFITSFilePDFTest() throws IOException {

        FITSClient fitsClient = new FITSClient();

        URL resource = getClass().getClassLoader().getResource("000009.pdf.fits.xml");
        List<CharacterisationResult> output = fitsClient.processFile(new File(resource.getPath()));

        Assert.assertEquals(32, output.size());
    }

    @Test
    void processFITSFileDocTest() throws IOException {

        FITSClient fitsClient = new FITSClient();

        URL resource = getClass().getClassLoader().getResource("002392.doc.fits.xml");
        List<CharacterisationResult> output = fitsClient.processFile(new File(resource.getPath()));

        Assert.assertEquals(23, output.size());
    }

    @Test
    void processFITSFileGZTest() throws IOException {

        FITSClient fitsClient = new FITSClient();

        URL resource = getClass().getClassLoader().getResource("002451.gz.fits.xml");
        List<CharacterisationResult> output = fitsClient.processFile(new File(resource.getPath()));

        Assert.assertEquals(13, output.size());
    }


    @Test
    void processFITSFilPDF2Test() throws IOException {

        FITSClient fitsClient = new FITSClient();

        URL resource = getClass().getClassLoader().getResource("002838.pdf.fits.xml");
        List<CharacterisationResult> output = fitsClient.processFile(new File(resource.getPath()));

        Assert.assertEquals(31, output.size());
    }

    @Test
    void processFITSFileTEXTest() throws IOException {

        FITSClient fitsClient = new FITSClient();

        URL resource = getClass().getClassLoader().getResource("002283.tex.fits.xml");
        List<CharacterisationResult> output = fitsClient.processFile(new File(resource.getPath()));

        Assert.assertEquals(11, output.size());
    }


    @Test
    void processFITSFilPDF3Test() throws IOException {

        FITSClient fitsClient = new FITSClient();

        URL resource = getClass().getClassLoader().getResource("002729.pdf.fits.xml");
        List<CharacterisationResult> output = fitsClient.processFile(new File(resource.getPath()));

        Assert.assertEquals(14, output.size());
    }

}