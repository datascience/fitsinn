package rocks.artur;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import rocks.artur.api_impl.CRH_ResolveConflictsImpl;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.jpa.CharacterisationResultGatewayJpaImpl;

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("h2")
@RunWith(SpringRunner.class)
@SpringBootTest
class CRHResolveConflictsImplTest {

    @Autowired
    CharacterisationResultGatewayJpaImpl characterisationResultGatewaySqlImpl;

    @Autowired
    CRH_ResolveConflictsImpl resolveConflicts;
    @Test
    void getAllTest() {

        Iterable<CharacterisationResult> characterisationResults =
                characterisationResultGatewaySqlImpl.getCharacterisationResults(null);
        double conflictRateBefore = characterisationResultGatewaySqlImpl.getConflictRate();
        List<CharacterisationResult> list = new ArrayList<>();
        characterisationResults.forEach(list::add);
        //Assert.assertEquals(10, list.size());

        resolveConflicts.run();

        double conflictRateAfter = characterisationResultGatewaySqlImpl.getConflictRate();

        System.out.println(String.format("Conflict rate: before - %4.3f, after - %4.3f", conflictRateBefore, conflictRateAfter));
    }
}