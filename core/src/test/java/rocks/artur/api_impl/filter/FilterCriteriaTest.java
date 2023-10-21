package rocks.artur.api_impl.filter;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;
import rocks.artur.domain.Source;
import rocks.artur.domain.Types;
import rocks.artur.domain.ValueType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


class FilterCriteriaTest {

    List<CharacterisationResult> repo = new ArrayList<>();

    @BeforeEach
    void prepare() {
        CharacterisationResult characterisationResult = new CharacterisationResult();
        characterisationResult.setProperty(Property.FORMAT);
        characterisationResult.setValue("HTML");
        characterisationResult.setFilePath("/home/user/file1");
        characterisationResult.setSource(Source.DROID3.toString());
        characterisationResult.setValueType(ValueType.STRING);
        repo.add(characterisationResult);

        characterisationResult = new CharacterisationResult();
        characterisationResult.setProperty(Property.MIMETYPE);
        characterisationResult.setValue("text/html");
        characterisationResult.setFilePath("/home/user/file1");
        characterisationResult.setSource(Source.DROID3.toString());
        characterisationResult.setValueType(ValueType.STRING);
        repo.add(characterisationResult);

        characterisationResult = new CharacterisationResult();
        characterisationResult.setProperty(Property.CREATED);
        characterisationResult.setValue(LocalDate.now().toString());
        characterisationResult.setFilePath("/home/user/file1");
        characterisationResult.setSource(Source.DROID3.toString());
        characterisationResult.setValueType(ValueType.TIMESTAMP);
        repo.add(characterisationResult);


        characterisationResult = new CharacterisationResult();
        characterisationResult.setProperty(Property.FORMAT);
        characterisationResult.setValue("PDF");
        characterisationResult.setFilePath("/home/user/file2");
        characterisationResult.setSource(Source.DROID3.toString());
        characterisationResult.setValueType(ValueType.STRING);
        repo.add(characterisationResult);

        characterisationResult = new CharacterisationResult();
        characterisationResult.setProperty(Property.MIMETYPE);
        characterisationResult.setValue("application/pdf");
        characterisationResult.setFilePath("/home/user/file2");
        characterisationResult.setSource(Source.DROID3.toString());
        characterisationResult.setValueType(ValueType.STRING);
        repo.add(characterisationResult);

        characterisationResult = new CharacterisationResult();
        characterisationResult.setProperty(Property.CREATED);
        characterisationResult.setValue(LocalDate.now().toString());
        characterisationResult.setFilePath("/home/user/file2");
        characterisationResult.setSource(Source.DROID3.toString());
        characterisationResult.setValueType(ValueType.TIMESTAMP);
        repo.add(characterisationResult);
    }

    /**
     * CharacterisationResult{property=FORMAT, value='HTML', valueType=STRING, source='DROID3', filePath='/home/user/file1'},
     * CharacterisationResult{property=MIMETYPE, value='text/html', valueType=STRING, source='DROID3', filePath='/home/user/file1'},
     * CharacterisationResult{property=CREATED, value='2022-04-22', valueType=TIMESTAMP, source='DROID3', filePath='/home/user/file1'},
     * CharacterisationResult{property=FORMAT, value='PDF', valueType=STRING, source='DROID3', filePath='/home/user/file2'},
     * CharacterisationResult{property=MIMETYPE, value='application/pdf', valueType=STRING, source='DROID3', filePath='/home/user/file2'},
     * CharacterisationResult{property=CREATED, value='2022-04-22', valueType=TIMESTAMP, source='DROID3', filePath='/home/user/file2'}
     */

    @Test
    void meetSingleCriteria() {

        FilterCriteria<CharacterisationResult> newFilter = new SingleFilterCriteria(Property.FORMAT, FilterOperation.EQUAL,
                "HTML");
        Assert.assertEquals("SingleFilterCriteria{searchKey=FORMAT, operation=EQUAL, searchValue='HTML'}", newFilter.toString());
        List<CharacterisationResult> characterisationResults = newFilter.meetCriteria(repo);
        Assert.assertEquals(2, characterisationResults.size());
    }

    @Test
    void meetSimpleAndCriteria() {

        FilterCriteria<CharacterisationResult> filterFormat = new SingleFilterCriteria(Property.FORMAT, FilterOperation.EQUAL,
                "HTML");
        FilterCriteria<CharacterisationResult> filterMimetype = new SingleFilterCriteria(Property.MIMETYPE, FilterOperation.EQUAL,
                "text/html");
        AndFilterCriteria<CharacterisationResult> andFilterCriteria = new AndFilterCriteria<>(filterFormat, filterMimetype);


        Assert.assertEquals("AndFilterCriteria{criteria=SingleFilterCriteria{searchKey=FORMAT, operation=EQUAL, searchValue='HTML'}, otherCriteria=SingleFilterCriteria{searchKey=MIMETYPE, operation=EQUAL, searchValue='text/html'}}", andFilterCriteria.toString());


        List<CharacterisationResult> characterisationResults = andFilterCriteria.meetCriteria(repo);
        Assert.assertEquals(0, characterisationResults.size());
    }

    @Test
    void meetSimpleOrCriteria() {

        FilterCriteria<CharacterisationResult> filterFormat = new SingleFilterCriteria(Property.FORMAT, FilterOperation.EQUAL,
                "HTML");
        FilterCriteria<CharacterisationResult> filterMimetype = new SingleFilterCriteria(Property.MIMETYPE, FilterOperation.EQUAL,
                "text/html");

        OrFilterCriteria<CharacterisationResult> orFilterCriteria = new OrFilterCriteria<>(filterFormat, filterMimetype);
        Assert.assertEquals("OrFilterCriteria{criteria=SingleFilterCriteria{searchKey=FORMAT, operation=EQUAL, searchValue='HTML'}, otherCriteria=SingleFilterCriteria{searchKey=MIMETYPE, operation=EQUAL, searchValue='text/html'}}", orFilterCriteria.toString());

        List<CharacterisationResult> characterisationResults = orFilterCriteria.meetCriteria(repo);
        Assert.assertEquals(4, characterisationResults.size());

    }

    @Test
    void meetComplexAndCriteria() {

        FilterCriteria<CharacterisationResult> filterFormat = new SingleFilterCriteria(Property.FORMAT, FilterOperation.EQUAL,
                "HTML");
        FilterCriteria<CharacterisationResult> filterMimetype = new SingleFilterCriteria(Property.MIMETYPE, FilterOperation.EQUAL,
                "text/html");
        FilterCriteria<CharacterisationResult> filterCreated = new SingleFilterCriteria(Property.CREATED, FilterOperation.EQUAL,
                "2022-04-22");

        AndFilterCriteria<CharacterisationResult> andFilterCriteria1 = new AndFilterCriteria<>(filterFormat, filterMimetype);

        AndFilterCriteria<CharacterisationResult> andFilterCriteria3 = new AndFilterCriteria<>(andFilterCriteria1, filterCreated);


        Assert.assertEquals("AndFilterCriteria{criteria=AndFilterCriteria{criteria=SingleFilterCriteria{searchKey=FORMAT, operation=EQUAL, searchValue='HTML'}, otherCriteria=SingleFilterCriteria{searchKey=MIMETYPE, operation=EQUAL, searchValue='text/html'}}, otherCriteria=SingleFilterCriteria{searchKey=CREATED, operation=EQUAL, searchValue='2022-04-22'}}", andFilterCriteria3.toString());


        List<CharacterisationResult> characterisationResults = andFilterCriteria3.meetCriteria(repo);
        Assert.assertEquals(0, characterisationResults.size());
    }


    @Test
    void meetComplexOrCriteria() {
        FilterCriteria<CharacterisationResult> filterFormat = new SingleFilterCriteria(Property.FORMAT, FilterOperation.EQUAL,
                "HTML");
        FilterCriteria<CharacterisationResult> filterMimetype = new SingleFilterCriteria(Property.MIMETYPE, FilterOperation.EQUAL,
                "text/html");
        FilterCriteria<CharacterisationResult> filterCreated = new SingleFilterCriteria(Property.CREATED, FilterOperation.EQUAL,
                "2022-04-22");

        AndFilterCriteria<CharacterisationResult> andFilterCriteria1 = new AndFilterCriteria<>(filterFormat, filterMimetype);

        OrFilterCriteria<CharacterisationResult> orFilterCriteria3 = new OrFilterCriteria<>(andFilterCriteria1, filterCreated);

        Assert.assertEquals("OrFilterCriteria{criteria=AndFilterCriteria{criteria=SingleFilterCriteria{searchKey=FORMAT, operation=EQUAL, searchValue='HTML'}, otherCriteria=SingleFilterCriteria{searchKey=MIMETYPE, operation=EQUAL, searchValue='text/html'}}, otherCriteria=SingleFilterCriteria{searchKey=CREATED, operation=EQUAL, searchValue='2022-04-22'}}", orFilterCriteria3.toString());

        List<CharacterisationResult> characterisationResults = orFilterCriteria3.meetCriteria(repo);
        Assert.assertEquals(2, characterisationResults.size());
    }

}