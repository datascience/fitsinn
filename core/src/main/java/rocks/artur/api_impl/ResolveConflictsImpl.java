package rocks.artur.api_impl;

import rocks.artur.api.ResolveConflicts;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.CharacterisationResultGateway;
import rocks.artur.domain.Property;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    Map<String, Double> sourceWeights;
    Map<CharacterisationResult, String> truth;

    void getData() {


        List<String[]> entries = characterisationResultGateway.getFilepathProperty();


        sources = characterisationResultGateway.getSources();

        sourceWeights = sources.stream().collect(Collectors.toMap(
                Function.identity(),
                s -> 1.0 / sources.size()));


        truth = new HashMap<>();
        for (String[] entry : entries) {
            List<CharacterisationResult> characterisationResults = characterisationResultGateway.getCharacterisationResultsByFilepathProperty(entry[0], Property.valueOf(entry[1]));

            if (characterisationResults.size() > 0) {
                CharacterisationResult firstResult = characterisationResults.get(0);
                Map<String, Double> votingScores = new HashMap<>();
                for (CharacterisationResult characterisationResult : characterisationResults) {
                    String source = characterisationResult.getSource();
                    Double sourceWeight = sourceWeights.get(source);
                    String value = characterisationResult.getValue();
                    if (votingScores.containsKey(value)) {
                        Double score = votingScores.get(value);
                        votingScores.put(value, score + sourceWeight);
                    } else {
                        votingScores.put(value, sourceWeight);
                    }
                }
                Optional<Map.Entry<String, Double>> first = votingScores.entrySet().stream().max(Map.Entry.comparingByValue());
                if (first.isPresent()) {
                    String trueValue = first.get().getKey();
                    truth.put(firstResult, trueValue);
                }


            }

        }

    }
}
