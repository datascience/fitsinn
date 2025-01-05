package rocks.artur.utils;

import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.Property;
import rocks.artur.domain.ValueType;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.UUID;

public class CharacterisationResultGenerator {

    public static String generateRandomString() {
        Integer nextInt = new Random().nextInt();
        return nextInt.toString();
    }

    public static String generateRandomString(int length) {
        Integer nextInt = new Random().nextInt(length);
        return nextInt.toString();
    }

    public static CharacterisationResult generate(){
        CharacterisationResult characterisationResult = new CharacterisationResult();
        characterisationResult.setFilePath(generateRandomString());
        characterisationResult.setProperty(Property.values()[new Random().nextInt(Property.values().length)]);
        characterisationResult.setSource(generateRandomString());
        characterisationResult.setValueType(characterisationResult.getProperty().getValueType());
        characterisationResult.setValue(generateValue(characterisationResult.getProperty().getValueType()));
        return characterisationResult;
    }

    private static String generateValue(ValueType valueType) {
        switch (valueType) {
            case STRING -> {
                return generateRandomString(100);
            }
            case BOOL -> {
                return new Random().nextBoolean() ? "true" : "false";
            }
            case INTEGER -> {
                return new Random().nextInt() % 2 == 0 ? "1" : "0";
            }
            case FLOAT -> {
                return new Random().nextFloat() % 2 == 0 ? "1" : "0";
            }
            case TIMESTAMP -> {
                return new Random().nextLong() % 2 == 0 ? "1" : "0";
            }
            case UID -> {
                return generateRandomString();
            }
            default -> throw new IllegalStateException("Unexpected value: " + valueType);
        }
    }
}
