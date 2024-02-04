package rocks.artur.endpoints;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import rocks.artur.domain.FilterCriteria;

import java.text.ParseException;

@ActiveProfiles("dev")
class CriteriaParserTest {

    @Test
    void parseBasic() throws ParseException {

        String s = "FORMAT=\"Portable Document Format\"";

        CriteriaParser parser = new CriteriaParser();

        FilterCriteria parse = parser.parse(s);

        System.out.println(parse);
    }

    @Test
    void parseDateLess() throws ParseException {

        String s = "FSLASTMODIFIED < \"2023-04-15\"";

        CriteriaParser parser = new CriteriaParser();

        FilterCriteria parse = parser.parse(s);

        System.out.println(parse);
    }
    @Test
    void parseDateLessOrEqual() throws ParseException {

        String s = "FSLASTMODIFIED <= \"2023-04-13\"";

        CriteriaParser parser = new CriteriaParser();

        FilterCriteria parse = parser.parse(s);

        System.out.println(parse);
    }

    @Test
    void parseOr() throws ParseException {

        String s = "format=\"docx\" OR format=\"pdf\"";

        CriteriaParser parser = new CriteriaParser();

        FilterCriteria parse = parser.parse(s);

        System.out.println(parse);
    }

    @Test
    void parseAnd() throws ParseException {

        String s = "FORMAT == \"JPEG File Interchange Format\" && MIMETYPE == \"image/jpeg\"";

        CriteriaParser parser = new CriteriaParser();

        FilterCriteria parse = parser.parse(s);

        System.out.println(parse);

        Assert.assertEquals("AndFilterCriteria{criteria=SingleFilterCriteria{searchKey=MIMETYPE, operation=EQUAL, searchValue='image/jpeg'}, otherCriteria=SingleFilterCriteria{searchKey=FORMAT, operation=EQUAL, searchValue='JPEG File Interchange Format'}}", parse.toString());
    }

    @Test
    void parseAndOr() throws ParseException {

        String s = "format=\"docx\" AND (mimetype=\"application/pdf\" or mimetype=\"application/doc\")";

        CriteriaParser parser = new CriteriaParser();

        FilterCriteria parse = parser.parse(s);

        System.out.println(parse);
    }



    void SpELTest() throws ParseException {

        String s = "property == 'format' && (value == 'pdf' || value == 'docx')";
        s = s.replace("(", " ( ").replace(")", " ) ").replace(" == ", "=");

        CriteriaParser parser = new CriteriaParser();

        FilterCriteria parse = parser.parse(s);

        System.out.println(parse);
    }
}


