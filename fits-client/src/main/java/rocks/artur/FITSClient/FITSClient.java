package rocks.artur.FITSClient;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import rocks.artur.FITSObjects.FITSPropertyJsonPath;
import rocks.artur.api.CharacterisationResultProducer;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.Property;
import rocks.artur.utils.JSONToolkit;
import rocks.artur.utils.STAXToolkit;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@ApplicationScoped
public class FITSClient implements CharacterisationResultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(FITSClient.class);
    List<String> knownProperties = Arrays.stream(FITSPropertyJsonPath.values()).map(Enum::name).collect(Collectors.toList());
    private String FITS_URL = "http://localhost:8888";
    @Override
    public String getVersion() throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(getFITS_URL() + "/version");
        return getString(httpclient.execute(httpGet));
    }


    public boolean isValid(File file) {

        try {
            Validator FITSValidator = initValidator("fits_output.xsd");
            FITSValidator.validate(new StreamSource(file));
            return true;
        } catch (SAXException | IOException e) {
            return false;
        }
    }

    public boolean isValid(byte[] file) {
        String content = new String(file);
        if (content.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\" ")) {
            return true;
        }
        return false;
    }

    public List<CharacterisationResult> processFile(byte[] file, String filename) throws IOException {

        if (isValid(file)) {
            try {
                String fitsSTRING = new String(file, StandardCharsets.UTF_8);
                return extractCharacterisationResultsStax(fitsSTRING);
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        } else {

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(getFITS_URL() + "/fits/examine");

            ByteArrayBody body = new ByteArrayBody(file, filename);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("datafile", body);
            HttpEntity reqEntity = builder.build();
            httppost.setEntity(reqEntity);

            CloseableHttpResponse response = httpclient.execute(httppost);
            String fitsResultXML = getString(response);
            LOG.debug(fitsResultXML);
            try {
                return extractCharacterisationResultsStax(fitsResultXML);
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<CharacterisationResult> processFile(File file) throws IOException {
        String fileName = file.getName();
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return processFile(fileContent, fileName);
    }

    List<CharacterisationResult> extractCharacterisationResults(String fitsResultXML) throws JSONException {
        List<CharacterisationResult> results = new ArrayList<>();
        String fitsResultJSON = JSONToolkit.translateXML(fitsResultXML);
        Set<String> availableFitsProperties = JSONToolkit.getAvailableFitsProperties(fitsResultJSON);

        availableFitsProperties.forEach(property -> {
            if (!knownProperties.contains(property.toUpperCase())) {
                LOG.error(String.format("Property '%s' is not known, please add it the known properties list", property.toUpperCase()));
            } else {
                List<CharacterisationResult> characterisationResults =
                        JSONToolkit.getCharacterisationResults(FITSPropertyJsonPath.valueOf(property.toUpperCase()),
                                fitsResultJSON);
                results.addAll(characterisationResults);
            }
        });

        List<CharacterisationResult> characterisationResults =
                JSONToolkit.getCharacterisationResults(FITSPropertyJsonPath.IDENTIFICATION, fitsResultJSON);
        results.addAll(characterisationResults);

        String filepath = results.stream().filter(result ->
                result.getProperty().equals(Property.FILEPATH)).findFirst().get().getValue().toString();
        addFilepathLabel(results, filepath);


        return results;
    }

     List<CharacterisationResult> extractCharacterisationResultsStax(String fitsResultXML) throws XMLStreamException {
        STAXToolkit staxToolkit = new STAXToolkit();
        return staxToolkit.getCharacterisationResults(fitsResultXML);

    }




    private void addFilepathLabel(List<CharacterisationResult> characterisationResults, String filepath) {
        characterisationResults.stream().forEach(result -> result.setFilePath(filepath));
    }

    private String getString(CloseableHttpResponse execute) throws IOException {
        CloseableHttpResponse response = execute;
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
        if (statusCode > 200) {
            response.close();
            throw new IOException(responseBody);
        }

        return responseBody;
    }


    public String getFITS_URL() {
        String fits_host = System.getenv("FITS_HOST");
        String fits_port = System.getenv("FITS_PORT");
        if (fits_host != null && !fits_host.isEmpty() &&
                fits_port != null && !fits_port.isEmpty()) {
            return String.format("http://%s:%s", fits_host, fits_port);
        }
        return FITS_URL;
    }

    public void setFITS_URL(String FITS_URL) {
        this.FITS_URL = FITS_URL;
    }


    private Validator initValidator(String xsdPath) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(getClass().getClassLoader().getResourceAsStream(xsdPath));
        Schema schema = factory.newSchema(schemaFile);
        return schema.newValidator();
    }

}
