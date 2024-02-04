package rocks.artur.utils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import net.minidev.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.artur.FITSObjects.*;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.Property;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class JSONToolkit {

    private static final Logger LOG = LoggerFactory.getLogger(JSONToolkit.class);
    public static int PRETTY_PRINT_INDENT_FACTOR = 4;
    static DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static String translateXML(String xmlString) throws JSONException {
        JSONObject xmlJSONObj = XML.toJSONObject(xmlString);
        String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
        return jsonPrettyPrintString;
    }

    public static Set<String> getAvailableFitsProperties(String jsonString) {
        Configuration configuration = Configuration.defaultConfiguration();
        MappingProvider mappingProvider = new JacksonMappingProvider();
        configuration = configuration.mappingProvider(mappingProvider);
        DocumentContext document = JsonPath.parse(jsonString, configuration);
        Set<String> result = new HashSet<>();
        try {
            Map mapFileinfo = document.read("$.fits.fileinfo", Map.class);
            result.addAll((Set<String>) mapFileinfo.keySet());
        } catch (com.jayway.jsonpath.PathNotFoundException | com.jayway.jsonpath.spi.mapper.MappingException ex) {
            LOG.debug("section $.fits.fileinfo not found");
        }
        try {
            Map mapFilestatus = document.read("$.fits.filestatus", Map.class);
            result.addAll((Set<String>) mapFilestatus.keySet());
            if (result.contains("well-formed")) {
                result.remove("well-formed");
                result.add("wellformed");
            }
        } catch (com.jayway.jsonpath.PathNotFoundException | com.jayway.jsonpath.spi.mapper.MappingException ex) {
            LOG.debug("section $.fits.filestatus not found");
        }


        try {
            Map mapMetadataDocument = document.read("$.fits.metadata.document", Map.class);
            result.addAll((Set<String>) mapMetadataDocument.keySet());
        } catch (com.jayway.jsonpath.PathNotFoundException | com.jayway.jsonpath.spi.mapper.MappingException ex) {
            LOG.debug("section $.fits.metadata.document not found");
        }

        try {
            Map mapMetadataText = document.read("$.fits.metadata.text", Map.class);
            result.addAll((Set<String>) mapMetadataText.keySet());
        } catch (com.jayway.jsonpath.PathNotFoundException | com.jayway.jsonpath.spi.mapper.MappingException ex) {
            LOG.debug("section $.fits.metadata.text not found");
        }


        return result;

    }


    public static List<CharacterisationResult> getCharacterisationResults(FITSPropertyJsonPath fitsProperty,
                                                                          String jsonString) {
        List<CharacterisationResult> result = new ArrayList<>();

        Configuration configuration = Configuration.defaultConfiguration();
        MappingProvider mappingProvider = new JacksonMappingProvider();
        configuration = configuration.mappingProvider(mappingProvider);
        DocumentContext document = JsonPath.parse(jsonString, configuration);
        if (fitsProperty.equals(FITSPropertyJsonPath.IDENTIFICATION)) {
            result.addAll(parseIdentification(fitsProperty, document));
        } else {
            result.addAll(parseGenericProperty(fitsProperty, document));
        }
        return result;
    }

    private static Collection<? extends CharacterisationResult> parseGenericProperty(FITSPropertyJsonPath jsonPath, DocumentContext document) {
        List<CharacterisationResult> result = new ArrayList<>();

        try {
            Object readObject = document.read(jsonPath.getFitsProperty());
            if (readObject instanceof JSONArray) {
                for (Object o : (JSONArray) readObject) {
                    Map<String, Object> objectMap = (Map) o;
                    GenericProperty gp = new GenericProperty();
                    gp.setProperty(jsonPath.name());
                    if (objectMap.containsKey("content")) {
                        gp.setContent(objectMap.get("content").toString());
                    }
                    if (objectMap.containsKey("status")) {
                        gp.setStatus(objectMap.get("status").toString());
                    }
                    if (objectMap.containsKey("toolname")) {
                        gp.setToolname(objectMap.get("toolname").toString());
                    }
                    if (objectMap.containsKey("toolversion")) {
                        gp.setToolversion(objectMap.get("toolversion").toString());
                    }

                    CharacterisationResult tmpResult = new CharacterisationResult();
                    setValues(tmpResult, Property.valueOf(jsonPath.name().toUpperCase()), gp.getContent());
                    tmpResult.setSource(gp.getToolname() + ":" + gp.getToolversion());
                    tmpResult = convertDataTypes(tmpResult);
                    result.add(tmpResult);
                }

            } else {
                GenericProperty read = document.read(jsonPath.getFitsProperty(), GenericProperty.class);
                CharacterisationResult tmpResult = new CharacterisationResult();
                setValues(tmpResult, Property.valueOf(jsonPath.name().toUpperCase()), read.getContent());
                tmpResult.setSource(read.getToolname() + ":" + read.getToolversion());
                tmpResult = convertDataTypes(tmpResult);
                result.add(tmpResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static CharacterisationResult convertDataTypes(CharacterisationResult tmpResult) {

        switch (tmpResult.getProperty()) {
            case CREATED:
            case FSLASTMODIFIED:
            case LASTMODIFIED:
                LOG.debug(String.format("Parsing Object: %s", tmpResult));
                if (tmpResult.getSource().startsWith("Exiftool")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ssXXX");
                        LocalDateTime parse = sdf.parse(tmpResult.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        tmpResult.setValue(parse.format(outputFormat));
                    } catch (ParseException e) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                            LocalDateTime parse = sdf.parse(tmpResult.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            tmpResult.setValue(parse.format(outputFormat));
                        } catch (ParseException ex) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mmXXX");
                                LocalDateTime parse = sdf.parse(tmpResult.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                                tmpResult.setValue(parse.format(outputFormat));
                            } catch (ParseException ex2) {
                                throw new RuntimeException(ex2);
                            }
                        }
                    }
                } else if (tmpResult.getSource().startsWith("NLNZ Metadata Extractor")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime parse = sdf.parse(tmpResult.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        tmpResult.setValue(parse.format(outputFormat));
                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if (tmpResult.getSource().startsWith("OIS File Information")) {
                    LocalDateTime triggerTime =
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(tmpResult.getValue())),
                                    TimeZone.getDefault().toZoneId());
                    tmpResult.setValue(triggerTime.format(outputFormat));
                } else if (tmpResult.getSource().startsWith("Tika")) {
                    DateTimeFormatter tikaFormatter = DateTimeFormatter.ISO_INSTANT;
                    Instant dateInstant = Instant.from(tikaFormatter.parse(tmpResult.getValue()));
                    LocalDateTime date = LocalDateTime.ofInstant(dateInstant, ZoneId.systemDefault());
                    tmpResult.setValue(date.format(outputFormat));

                }
                LOG.debug(String.format("Parsed Result: %s", tmpResult));
                break;
        }

        return tmpResult;
    }

    private static List<CharacterisationResult> parseIdentification(FITSPropertyJsonPath jsonPath,
                                                                    DocumentContext document) {

        List<CharacterisationResult> result = new ArrayList<>();
        Map<String, Object> read = (Map<String, Object>) document.read(jsonPath.getFitsProperty(), Map.class);
        //Remove entries which are not Identity
        read.entrySet().removeIf(entry -> !entry.getKey().equals("identity"));
        Object identity = read.get("identity");
        if (identity instanceof List) {
            List identities = (List) identity;
            if (identities.size() == 1) {  // There is only 1 identity
                extractIdentity(document, jsonPath.getFitsProperty() + ".identity", result, String.format(jsonPath.getFitsProperty() + ".identity.externalIdentifier"));
            } else {  //There are many identities
                for (int i = 0; i < identities.size(); i++) {
                    extractIdentity(document, String.format(jsonPath.getFitsProperty() + ".identity.[%d]", i), result, String.format(jsonPath.getFitsProperty() + ".identity.[%d].externalIdentifier", i));
                }
            }
        } else {
            extractIdentity(document, jsonPath.getFitsProperty() + ".identity", result, jsonPath.getFitsProperty() + ".identity.externalIdentifier");
        }
        return result;
    }

    private static void extractIdentity(DocumentContext document, String jsonPath, List<CharacterisationResult> result, String externalIdentifierPath) {
        IdentityProperty identityProperty = document.read(jsonPath, IdentityProperty.class);
        List<CharacterisationResult> formatData = getFormatData(document, jsonPath);
        List<CharacterisationResult> mimetypeData = getMimetypeData(document, jsonPath);
        result.addAll(formatData);
        result.addAll(mimetypeData);
        extractExternalIdentifier(identityProperty, document, result, externalIdentifierPath);
    }

    private static void extractExternalIdentifier(IdentityProperty identityProperty, DocumentContext document, List<CharacterisationResult> result, String jsonPath1) {
        if (identityProperty.getExternalIdentifier() != null) {
            if (identityProperty.getExternalIdentifier() instanceof List) {
                List externalIdentifiers = (List) identityProperty.getExternalIdentifier();
                for (int i = 0; i < externalIdentifiers.size(); i++) {
                    ExternalIdentifierProperty genericProperty = document.read(String.format(jsonPath1 + ".[%d]", i), ExternalIdentifierProperty.class);
                    CharacterisationResult tmpResult = new CharacterisationResult();
                    setValues(tmpResult, Property.EXTERNALIDENTIFIER, genericProperty.getContent());
                    tmpResult.setSource(genericProperty.getToolname() + ":" + genericProperty.getToolversion());
                    result.add(tmpResult);
                }
            } else {
                ExternalIdentifierProperty genericProperty = document.read(jsonPath1, ExternalIdentifierProperty.class);
                CharacterisationResult tmpResult = new CharacterisationResult();
                setValues(tmpResult, Property.EXTERNALIDENTIFIER, genericProperty.getContent());
                tmpResult.setSource(genericProperty.getToolname() + ":" + genericProperty.getToolversion());
                result.add(tmpResult);
            }
        }
    }

    private static List<CharacterisationResult> getFormatData(DocumentContext document, String jsonPath) {
        List<CharacterisationResult> result = new ArrayList<>();
        IdentityProperty identityProperty = document.read(jsonPath, IdentityProperty.class);

        List<ToolIdentity> toolData = getToolData(document, jsonPath);
        List<GenericProperty> versionData = getVersionData(document, jsonPath);

        for (ToolIdentity tool : toolData) {

            List<GenericProperty> matchingVersions =
                    versionData.stream().filter(version -> version.getToolname().equals(tool.getToolname()) && version.getToolversion().equals(tool.getToolversion())).collect(Collectors.toList());

            if (matchingVersions.size() > 0) {
                for (GenericProperty matchingVersion : matchingVersions) {
                    CharacterisationResult tmpResult = new CharacterisationResult();
                    setValues(tmpResult, Property.FORMAT_VERSION, identityProperty.getFormat() + " " + matchingVersion.getContent());
                    tmpResult.setSource(tool.getToolname() + ":" + tool.getToolversion());
                    result.add(tmpResult);

                    CharacterisationResult tmpResult2 = new CharacterisationResult();
                    setValues(tmpResult2, Property.FORMAT, identityProperty.getFormat());
                    tmpResult2.setSource(tool.getToolname() + ":" + tool.getToolversion());
                    result.add(tmpResult2);
                }
            } else {
                CharacterisationResult tmpResult = new CharacterisationResult();
                setValues(tmpResult, Property.FORMAT, identityProperty.getFormat());
                tmpResult.setSource(tool.getToolname() + ":" + tool.getToolversion());
                result.add(tmpResult);
            }

        }
        return result;
    }


    private static List<CharacterisationResult> getMimetypeData(DocumentContext document, String jsonPath) {
        List<CharacterisationResult> result = new ArrayList<>();
        IdentityProperty identityProperty = document.read(jsonPath, IdentityProperty.class);

        List<ToolIdentity> toolData = getToolData(document, jsonPath);

        for (ToolIdentity tool : toolData) {
            CharacterisationResult tmpResult = new CharacterisationResult();
            setValues(tmpResult, Property.MIMETYPE, identityProperty.getMimetype());
            tmpResult.setSource(tool.getToolname() + ":" + tool.getToolversion());
            result.add(tmpResult);
        }
        return result;
    }


    private static List<ToolIdentity> getToolData(DocumentContext document, String jsonPath) {

        IdentityProperty identityProperty = document.read(jsonPath, IdentityProperty.class);

        Object tool = identityProperty.getTool();

        List<ToolIdentity> tools = new ArrayList<>();
        if (tool != null) {
            if (tool instanceof List) {
                List toolArray = (List) tool;

                for (int j = 0; j < toolArray.size(); j++) {
                    ToolIdentity toolIdentity = document.read(String.format(jsonPath + ".tool[%d]", j), ToolIdentity.class);
                    tools.add(toolIdentity);
                }
            } else {
                ToolIdentity toolIdentity = document.read(jsonPath + ".tool", ToolIdentity.class);
                tools.add(toolIdentity);
            }
        }
        return tools;
    }

    private static List<GenericProperty> getVersionData(DocumentContext document,
                                                        String jsonPath) {

        IdentityProperty identityProperty = document.read(jsonPath, IdentityProperty.class);

        Object version = identityProperty.getVersion();

        List<GenericProperty> versions = new ArrayList<>();
        if (version != null) {
            if (version instanceof List) {
                List versionArray = (List) version;

                for (int j = 0; j < versionArray.size(); j++) {
                    GenericProperty read = document.read(String.format(jsonPath + ".version[%d]", j),
                            GenericProperty.class);
                    versions.add(read);
                }
            } else {
                GenericProperty read = document.read(jsonPath + ".version", GenericProperty.class);
                versions.add(read);
            }
        }
        return versions;
    }


    private static void setValues(CharacterisationResult tmpResult, Property format, String value) {
        tmpResult.setProperty(format);
        tmpResult.setValueType(format.getValueType());
        tmpResult.setValue(value);
    }
}
