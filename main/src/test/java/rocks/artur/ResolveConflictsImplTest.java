package rocks.artur;


import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import rocks.artur.api_impl.ResolveConflictsImpl;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;
import rocks.artur.domain.SamplingAlgorithms;
import rocks.artur.domain.statistics.PropertyStatistic;
import rocks.artur.domain.statistics.PropertyValueStatistic;
import rocks.artur.endpoints.CriteriaParser;
import rocks.artur.jpa.CharacterisationResultGatewayJpaImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest
class ResolveConflictsImplTest {

    @Autowired
    CharacterisationResultGatewayJpaImpl characterisationResultGatewaySqlImpl;

    @Autowired
    ResolveConflictsImpl resolveConflicts;
    @Test
    void getAllTest() {

        Iterable<CharacterisationResult> characterisationResults =
                characterisationResultGatewaySqlImpl.getCharacterisationResults(null);

        List<CharacterisationResult> list = new ArrayList<>();
        characterisationResults.forEach(list::add);
        Assert.assertEquals(10, list.size());

        resolveConflicts.run();
    }
}