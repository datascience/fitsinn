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
import rocks.artur.api_impl.utils.ByteFile;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.Property;
import rocks.artur.domain.ValueType;
import rocks.artur.utils.JSONToolkit;
import rocks.artur.utils.STAXToolkit;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

//@ApplicationScoped
public class FITSClient implements CharacterisationResultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(FITSClient.class);
    List<String> knownProperties = Arrays.stream(FITSPropertyJsonPath.values()).map(Enum::name).collect(Collectors.toList());
    private String FITS_URL = "http://localhost:8888";


    static DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static DateTimeFormatter inputFormatter = new DateTimeFormatterBuilder()
            .appendPattern("[yyyy:MM:dd HH:mm:ssXXX][yyyy:MM:dd HH:mm:ss][yyyy:MM:dd HH:mmXXX][yyyy-MM-dd HH:mm:ss][yyyy/MM/dd HH:mm:ss]")
            .toFormatter();

    @Override
    public String getVersion(){

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(getFITS_URL() + "/version");
        try {
            return getString(httpclient.execute(httpGet));
        } catch (IOException e) {
            LOG.error("Exception occurred when querying the FITS version");
            e.printStackTrace();
        }
        return "";
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

        try {
            Validator FITSValidator = initValidator("fits_output.xsd");
            FITSValidator.validate(new StreamSource(new ByteArrayInputStream(file)));
            return true;
        } catch (SAXException | IOException e) {
            return false;
        }
    }

    public boolean isValid(String content) {
        return content.contains("xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\"");
    }

    @Override
    public List<CharacterisationResult> processFile(File file) {
        String fileName = file.getName();
        byte[] fileContent = new byte[0];
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            LOG.error("Exception occurred during file processing");
            e.printStackTrace();
        }
        ByteFile byteFile = new ByteFile(fileContent, fileName);
        return processFile(byteFile);
    }

    @Override
    public List<CharacterisationResult> processFile(ByteFile file)  {
        ArrayList<CharacterisationResult> result = new ArrayList<>();
        if (file.getFile().length == 0) {
            return result;
        }
        try {
            String content = new String(file.getFile());
            if (!isValid(content)) {
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost(getFITS_URL() + "/fits/examine");
                ByteArrayBody body = new ByteArrayBody(file.getFile(), file.getFilename());
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addPart("datafile", body);
                HttpEntity reqEntity = builder.build();
                httppost.setEntity(reqEntity);
                CloseableHttpResponse response = httpclient.execute(httppost);

                content = getString(response);
                LOG.debug(content);
            }
            result.addAll(extractCharacterisationResultsStax(content));
        } catch (Exception e) {
            LOG.error("Exception occurred during FITS file parsing");
            e.printStackTrace();
        }

        result=this.fixDateTypes(result);
        return result;
    }

    private ArrayList<CharacterisationResult> fixDateTypes(ArrayList<CharacterisationResult> result) {
        result.stream().forEach(item -> {
            if (item.getValueType().equals(ValueType.TIMESTAMP)){
                String value = item.getValue();
                LOG.debug(String.format("Parsing Object: %s", item));
                if (item.getSource().startsWith("OIS File Information")) {
                    LocalDateTime parsed =
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(value)),
                                    TimeZone.getDefault().toZoneId());
                    item.setValue(parsed.format(outputFormatter));
                } else {
                    LocalDateTime parsed = tryParseLocalDateTime(value, inputFormatter);
                    if (parsed != null) {
                        item.setValue(parsed.format(outputFormatter));
                    } else {
                        item.setValue(null);
                    }
                }
                LOG.debug(String.format("Parsed Result: %s", item));
            }
        });
        return result;
    }

     LocalDateTime tryParseLocalDateTime(String datetimeString, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(datetimeString, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
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

        //String filepath = results.stream().filter(result ->
       //         result.getProperty().equals(Property.FILEPATH)).findFirst().get().getValue().toString();
        //addFilepathLabel(results, filepath);


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
