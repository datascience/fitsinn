package rocks.artur.FITSClient;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import rocks.artur.FITSObjects.FITSPropertyJsonPath;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.utils.JSONToolkit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

class JSONToolkitTest {

    @Test
    void translateXMLTest() throws JSONException {
        String s = JSONToolkit.translateXML(FITSClientTest.VALID_FITS_RESULT);
        System.out.println(s);
    }

    @Test
    void getCharacterisationResult2Test() throws JSONException {
        String jsonString = JSONToolkit.translateXML(FITSClientTest.VALID_FITS_RESULT2);
        List<CharacterisationResult> results = JSONToolkit.getCharacterisationResults(FITSPropertyJsonPath.IDENTIFICATION,
                jsonString);
        System.out.println(results);
    }

    @Test
    void getCharacterisationResultTest() throws JSONException {
        String jsonString = JSONToolkit.translateXML(FITSClientTest.VALID_FITS_RESULT);
        List<CharacterisationResult> results = JSONToolkit.getCharacterisationResults(FITSPropertyJsonPath.FILENAME,
                jsonString);

        Assert.assertEquals("CharacterisationResult{property=FILENAME, value='README.md', valueType=STRING, source='OIS File Information:1', filePath='null'}", results.get(0).toString());
    }


    @Test
    void getCharacterisationResultIdentificationTest() throws JSONException {
        String jsonString = JSONToolkit.translateXML(FITSClientTest.VALID_FITS_RESULT);
        List<CharacterisationResult> results = JSONToolkit.getCharacterisationResults(FITSPropertyJsonPath.IDENTIFICATION,
                jsonString);

        System.out.println(results);
        Assert.assertEquals(7, results.size());
    }

    @Test
    void getAvailableFitsPropertiesTest() throws JSONException {
        String jsonString = JSONToolkit.translateXML(FITSClientTest.VALID_FITS_RESULT3);
        Set<String> availableFitsProperties = JSONToolkit.getAvailableFitsProperties(jsonString);
        List<Object> objects = Arrays.asList(availableFitsProperties.toArray());

        Assert.assertEquals("[pageCount, isRightsManaged, created, author, hasAnnotations, title, fslastmodified, valid, isTagged, wellformed, filename, isProtected, size, creatingApplicationName, filepath, lastmodified, md5checksum, hasForms, hasOutline]", objects.toString());
    }

    @Test
    void dateTimeFormatterTest() throws JSONException {
        //SELECT PROPERTY_VALUE, PARSEDATETIME(PROPERTY_VALUE,'dd-MM-yyyy HH:mm:ss')   FROM CHARACTERISATIONRESULTVIEW
        //where property='FSLASTMODIFIED'
        //
        DateTimeFormatter tikaFormatter = DateTimeFormatter.ISO_INSTANT;
        Instant dateInstant = Instant.from(tikaFormatter.parse("2008-06-04T22:47:36Z"));
        LocalDateTime date = LocalDateTime.ofInstant(dateInstant, ZoneId.of(ZoneOffset.UTC.getId()));
        System.out.println(date);;

    }
}