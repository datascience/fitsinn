package rocks.artur.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.Property;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.security.KeyPairGenerator;
import java.util.*;

public class STAXToolkit {

    private final Logger LOG = LoggerFactory.getLogger(STAXToolkit.class);
    Map<String, String> identities = new HashMap<>(); // [<format,Portable Network Graphics>, <mimetype,image/png>]
    List<String> sources = new ArrayList<>(); // [<Droid,6.4>, <Exiftool,11.54>]
    String filepath;
    String propertyValue;
    List<CharacterisationResult> results = new ArrayList<>();

    public List<CharacterisationResult> getCharacterisationResults(String fitsResultXML) throws XMLStreamException {

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(fitsResultXML));

        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    QName elementName = reader.getName();
                    handleStartElement(elementName, reader);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    QName endElementName = reader.getName();
                    handleEndElement(endElementName, reader);
                    break;
                case XMLStreamConstants.CHARACTERS:
                    String text = reader.getText().trim();
                    handleText(text);
                    break;
            }
        }
        results.forEach(item -> item.setFilePath(filepath));

        return results;
    }

    private void handleStartElement(QName elementName, XMLStreamReader reader) throws XMLStreamException {
        // Add your logic to handle specific elements
        String elementNameLocalPart = elementName.getLocalPart();
        System.out.println("Start Element: " + elementNameLocalPart);

        switch (elementNameLocalPart) {
            case "identity":
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    String attributeName = reader.getAttributeName(i).getLocalPart();
                    String attributeValue = reader.getAttributeValue(i);
                    System.out.println(" - Attribute: " + attributeName + "=" + attributeValue);
                    if (attributeName.equals("format") || attributeName.equals("mimetype")) {
                        identities.put(attributeName, attributeValue);
                    }
                }
                break;
            case "tool":
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    String attributeName = reader.getAttributeName(i).getLocalPart();
                    String attributeValue = reader.getAttributeValue(i);
                    System.out.println(" - Attribute: " + attributeName + "=" + attributeValue);

                    if ("toolname".equals(attributeName)) {
                        sources.add(attributeValue);
                    }
                    if ("toolversion".equals(attributeName)) {
                        String toolname = sources.get(sources.size() - 1);
                        toolname += ":" + attributeValue;
                        sources.set(sources.size() - 1, toolname);
                    }
                }
                break;
            case "version":
                String version_source = "";
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    String attributeName = reader.getAttributeName(i).getLocalPart();
                    String attributeValue = reader.getAttributeValue(i);
                    System.out.println(" - Attribute: " + attributeName + "=" + attributeValue);
                    if ("toolname".equals(attributeName)) {
                        version_source = attributeValue;
                    }
                    if ("toolversion".equals(attributeName)) {
                        version_source += ":" + attributeValue;
                    }
                }
                CharacterisationResult resVersion = new CharacterisationResult();
                resVersion.setProperty(Property.FORMAT_VERSION);
                resVersion.setSource(version_source);
                resVersion.setValueType(Property.FORMAT_VERSION.getValueType());
                results.add(resVersion);
                break;

            case "well-formed":
                String sourceWellformed = "";
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    String attributeName = reader.getAttributeName(i).getLocalPart();
                    String attributeValue = reader.getAttributeValue(i);
                    System.out.println(" - Attribute: " + attributeName + "=" + attributeValue);
                    if ("toolname".equals(attributeName)) {
                        sourceWellformed = attributeValue;
                    }
                    if ("toolversion".equals(attributeName)) {
                        sourceWellformed += ":" + attributeValue;
                    }
                }
                CharacterisationResult resWellformed = new CharacterisationResult();
                resWellformed.setProperty(Property.WELLFORMED);
                resWellformed.setSource(sourceWellformed);
                resWellformed.setValueType(Property.WELLFORMED.getValueType());
                results.add(resWellformed);
                break;
            default:
                String property = elementNameLocalPart;
                boolean isPresent = Arrays.stream(Property.values()).anyMatch(item -> property.equalsIgnoreCase(item.name()));
                if (isPresent) {
                    String source = "";
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        String attributeName = reader.getAttributeName(i).getLocalPart();
                        String attributeValue = reader.getAttributeValue(i);
                        System.out.println(" - Attribute: " + attributeName + "=" + attributeValue);
                        if ("toolname".equals(attributeName)) {
                            source = attributeValue;
                        }
                        if ("toolversion".equals(attributeName)) {
                            source += ":" + attributeValue;
                        }
                    }
                    CharacterisationResult new_res = new CharacterisationResult();
                    Property propertyEnum = Property.valueOf(property.toUpperCase());
                    new_res.setProperty(propertyEnum);
                    new_res.setSource(source);
                    new_res.setValueType(propertyEnum.getValueType());
                    results.add(new_res);
                }


        }

    }

    private void handleEndElement(QName endElementName, XMLStreamReader reader) {
        // Add your logic to handle specific end elements
        String elementNameLocalPart = endElementName.getLocalPart();
        System.out.println("End Element: " + elementNameLocalPart);


        switch (elementNameLocalPart) {

            case "identity":
                for (Map.Entry<String, String> identity : identities.entrySet()) {
                    String property = identity.getKey();
                    String value = identity.getValue();

                    for (String source : sources) {
                        CharacterisationResult new_res = new CharacterisationResult();
                        Property propertyEnum = Property.valueOf(property.toUpperCase());
                        new_res.setProperty(propertyEnum);
                        new_res.setValue(value);
                        new_res.setSource(source);
                        new_res.setValueType(propertyEnum.getValueType());
                        results.add(new_res);
                    }
                }
                identities.clear();
                sources.clear();
                break;
            case "version":
                CharacterisationResult characterisationResultVersion = results.get(results.size() - 1);
                String format = identities.get("format");
                characterisationResultVersion.setValue(String.format("%s %s",format,propertyValue));
                break;
            case "fits":
                results.forEach(res -> res.setFilePath(filepath));
                break;
            case "well-formed":
                CharacterisationResult characterisationResultWellformed = results.get(results.size() - 1);
                characterisationResultWellformed.setValue(propertyValue);
                break;
            case "filepath":
                filepath = propertyValue;
            default:
                String property = elementNameLocalPart;
                boolean isPresent = Arrays.stream(Property.values()).anyMatch(item -> property.equalsIgnoreCase(item.name()));
                if (isPresent) {
                    CharacterisationResult characterisationResult = results.get(results.size() - 1);
                    characterisationResult.setValue(propertyValue);
                }

        }

    }

    private void handleText(String text) {
        System.out.println("Text: " + text);
        propertyValue = text;
    }
}
