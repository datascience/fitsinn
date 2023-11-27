package rocks.artur.api_impl;

import rocks.artur.api.ResolveConflicts;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.CharacterisationResultGateway;
import rocks.artur.domain.Property;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResolveConflictsImpl implements ResolveConflicts {
    private CharacterisationResultGateway characterisationResultGateway;

    @Override
    public void run() {


        getData();
        generateTruth();
        updateWeights();
    }

    private void updateWeights() {
    }

    private void generateTruth() {

    }

    List<String> sources;
    Double[] sourceWeights;

    void getData() {


        List<String[]> entries = characterisationResultGateway.getFilepathProperty();


        sources = characterisationResultGateway.getSources();

        Map<String, Double> sourceWeights = sources.stream().collect(Collectors.toMap(
                Function.identity(),
                s -> 1.0 / sources.size()));


        List<CharacterisationResult> characterisationResultsAll = characterisationResultGateway.getCharacterisationResults(null);
        Map<CharacterisationResult, Double> weightMatrix = characterisationResultsAll.stream().collect(Collectors.toMap(Function.identity(), s -> 0.0));

        for (String[] entry : entries) {
            List<CharacterisationResult> characterisationResults = characterisationResultGateway.getCharacterisationResultsByFilepathProperty(entry[0], Property.valueOf(entry[1]));

            if (characterisationResults.size() > 0) {
                CharacterisationResult firstResult = characterisationResults.get(0);
                switch (firstResult.getValueType()) {

                    case STRING -> {

                    }
                    case BOOL -> {
                    }
                    case INTEGER -> {
                    }
                    case FLOAT -> {
                    }
                    case TIMESTAMP -> {
                    }
                    case UID -> {
                    }
                }
            }

        }

    }
}
