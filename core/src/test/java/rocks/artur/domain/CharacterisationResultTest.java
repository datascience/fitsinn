package rocks.artur.domain;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class CharacterisationResultTest {

    @Test
    void testEquals() {
        CharacterisationResult obj1 = new CharacterisationResult();
        CharacterisationResult obj2 = new CharacterisationResult();

        Assert.assertTrue(obj1.equals(obj1));
        Assert.assertTrue(obj1.equals(obj2));
        Assert.assertFalse(obj1.equals(null));

        obj1.setSource("sourceA");
        obj2.setSource("sourceB");
        Assert.assertFalse(obj1.equals(obj2));

        obj1.setSource("sourceA");
        obj2.setSource("sourceA");
        Assert.assertTrue(obj1.equals(obj2));


        obj1.setSource("sourceA");
        obj2.setSource("sourceA");
        obj1.setFilePath("path1");
        obj2.setFilePath("path1");
        Assert.assertTrue(obj1.equals(obj2));

        obj1.setProperty(Property.FORMAT);
        obj2.setProperty(Property.MIMETYPE);
        Assert.assertFalse(obj1.equals(obj2));

        obj1.setProperty(Property.FORMAT);
        obj2.setProperty(Property.FORMAT);
        Assert.assertTrue(obj1.equals(obj2));



        obj1.setValue("val1");
        obj2.setValue("val2");
        Assert.assertFalse(obj1.equals(obj2));

    }
}