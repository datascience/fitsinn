package rocks.artur.api_impl;

import rocks.artur.api.ResolveConflicts;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.CharacterisationResultGateway;
import rocks.artur.domain.Entry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResolveConflictsImpl implements ResolveConflicts {


    private CharacterisationResultGateway characterisationResultGateway;

    public ResolveConflictsImpl(CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultGateway = characterisationResultGateway;
    }

    @Override
    public void run() {
        init();
        System.out.println(sourceWeights);
        //System.out.println("sum of weights: " +  sourceWeights.values().stream().reduce(0d, Double::sum));
        updateTruth();
        System.out.println("sum of weights: " +  sourceWeights.values().stream().reduce(0d, Double::sum));
        //System.out.println(truth);
        for (int i = 0; i < 3; i++) {
            updateWeights();
            System.out.println(sourceWeights);
            System.out.println("sum of weights: " +  sourceWeights.values().stream().reduce(0d, Double::sum));
            updateTruth();
            //System.out.println(truth);
        }

        resolveConflicts();
    }

    private void resolveConflicts() {
        truth.entrySet().stream().forEach( entry -> {
            Entry key = entry.getKey();
            String value = entry.getValue();

            List<CharacterisationResult> characterisationResultsByEntry = characterisationResultGateway.getCharacterisationResultsByEntry(key);
            for (CharacterisationResult characterisationResult : characterisationResultsByEntry) {
                if (!characterisationResult.getValue().equals(value)) {
                    characterisationResultGateway.delete(characterisationResult);
                }
            }


        });
    }

    private void updateWeights() {
        Map<String, Double> score = sources.stream().collect(Collectors.toMap(
                Function.identity(),
                s -> 0.0));

        Map<String, Double> count = sources.stream().collect(Collectors.toMap(
                Function.identity(),
                s -> 0.0));


        List<Entry> entries = characterisationResultGateway.getEntries();

        for (Entry entry : entries) {
            List<CharacterisationResult> characterisationResults = characterisationResultGateway.getCharacterisationResultsByEntry(entry);

            for (CharacterisationResult characterisationResult : characterisationResults) {

                String trueValue = truth.get(entry);

                String value = characterisationResult.getValue();
                String source = characterisationResult.getSource();
                if (value.equals(trueValue)) {
                    score.put(source, score.getOrDefault(source, 0.0) + 0);
                } else {
                    score.put(source, score.getOrDefault(source, 0.0) + 1);
                }
                count.put(source, count.getOrDefault(source, 0.0) + 1);
            }
        }
        for (String source : score.keySet()) {
            Double countSource = count.getOrDefault(source, 1.0);
            if (countSource == 0 ) {
                score.put(source, 0d);
            } else {
                score.put(source, score.get(source) / countSource);
            }
        }
        Double sum = score.values().stream().reduce(0.0, (a, b) -> a + b);

        score.replaceAll((s, v) -> score.get(s) / sum);

        //Optional<Map.Entry<String, Double>> max = score.entrySet().stream().max(Map.Entry.comparingByValue());
        //if (max.isPresent()) {
           // Double norm_score = max.get().getValue();
            for (String source : score.keySet()) {
               // double w = score.get(source) / norm_score;
                Double weig = score.get(source);
                if (weig == 0d) {
                    sourceWeights.put(source,0.00001);
                } else {
                    sourceWeights.put(source, -Math.log(weig));
                }
         //   }
        }
    }

    private void updateTruth() {
        List<Entry> entries = characterisationResultGateway.getEntries();
        for (Entry entry : entries) {
            List<CharacterisationResult> characterisationResults = characterisationResultGateway.getCharacterisationResultsByEntry(entry);

            if (characterisationResults.size() > 0) {
                CharacterisationResult firstResult = characterisationResults.get(0);
                Map<String, Double> votingScores = new HashMap<>();
                for (CharacterisationResult characterisationResult : characterisationResults) {
                    String source = characterisationResult.getSource();
                    Double sourceWeight = sourceWeights.get(source);
                    String value = characterisationResult.getValue();

                    votingScores.put(value, votingScores.getOrDefault(value, 0.0) + sourceWeight);
                }
                Optional<Map.Entry<String, Double>> first = votingScores.entrySet().stream().max(Map.Entry.comparingByValue());
                if (first.isPresent()) {
                    String trueValue = first.get().getKey();
                    truth.put(entry, trueValue);
                }
            }
        }
    }

    List<String> sources;
    Map<String, Double> sourceWeights;
    Map<Entry, String> truth;

    void init() {

        sources = characterisationResultGateway.getSources();
        sourceWeights = sources.stream().collect(Collectors.toMap(
                Function.identity(),
                s -> 1.0 / sources.size()));
        truth = new HashMap<>();


    }
}
