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

public class CRH_ResolveConflictsImpl {//implements ResolveConflicts {


    private CharacterisationResultGateway characterisationResultGateway;

    public CRH_ResolveConflictsImpl(CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultGateway = characterisationResultGateway;
    }


    public void run(String datasetName) {
        init(datasetName);
        System.out.println(sourceWeights);
        //System.out.println("sum of weights: " +  sourceWeights.values().stream().reduce(0d, Double::sum));
        updateTruth(datasetName);
        System.out.println("sum of weights: " +  sourceWeights.values().stream().reduce(0d, Double::sum));
        //System.out.println(truth);
        for (int i = 0; i < 3; i++) {
            updateWeights(datasetName);
            System.out.println(sourceWeights);
            System.out.println("sum of weights: " +  sourceWeights.values().stream().reduce(0d, Double::sum));
            updateTruth(datasetName);
            //System.out.println(truth);
        }

        resolveConflicts(datasetName);
    }

    private void resolveConflicts(String datasetName) {
        truth.entrySet().stream().forEach( entry -> {
            Entry key = entry.getKey();
            String value = entry.getValue();

            List<CharacterisationResult> characterisationResultsByEntry = characterisationResultGateway.getCharacterisationResultsByEntry(key, datasetName);
            for (CharacterisationResult characterisationResult : characterisationResultsByEntry) {
                if (!characterisationResult.getValue().equals(value)) {
                    characterisationResultGateway.delete(characterisationResult, datasetName);
                }
            }


        });
    }

    private void updateWeights(String datasetName) {
        Map<String, Double> score = sources.stream().collect(Collectors.toMap(
                Function.identity(),
                s -> 0.0));

        Map<String, Double> count = sources.stream().collect(Collectors.toMap(
                Function.identity(),
                s -> 0.0));


        List<Entry> entries = characterisationResultGateway.getEntries(datasetName);

        for (Entry entry : entries) {
            List<CharacterisationResult> characterisationResults = characterisationResultGateway.getCharacterisationResultsByEntry(entry, datasetName);

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

        Optional<Map.Entry<String, Double>> max = score.entrySet().stream().max(Map.Entry.comparingByValue());
        if (max.isPresent()) {
            Double norm_score = max.get().getValue();
            for (String source : score.keySet()) {
                double w = score.get(source) / norm_score;
                Double weig = score.get(source);
                if (w == 0d) {
                    sourceWeights.put(source,0.00001);
                } else {
                    sourceWeights.put(source, -Math.log(w));
                }
            }
        }
    }

    private void updateTruth(String datasetName) {
        List<Entry> entries = characterisationResultGateway.getEntries(datasetName);
        for (Entry entry : entries) {
            List<CharacterisationResult> characterisationResults = characterisationResultGateway.getCharacterisationResultsByEntry(entry, datasetName);

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

    void init(String datasetName) {

        sources = characterisationResultGateway.getSources(datasetName);
        sourceWeights = sources.stream().collect(Collectors.toMap(
                Function.identity(),
                s -> 1.0 / sources.size()));
        truth = new HashMap<>();


    }
}
