package rocks.artur.api_impl;

import rocks.artur.api.GetSamples;
import rocks.artur.domain.CharacterisationResultGateway;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;
import rocks.artur.domain.SamplingAlgorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetSamplesImpl implements GetSamples {

    private CharacterisationResultGateway characterisationResultGateway;


    public GetSamplesImpl(CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultGateway = characterisationResultGateway;
    }

    private SamplingAlgorithms algorithm;

    private List<Property> properties;

    @Override
    public void setAlgorithm(SamplingAlgorithms algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public List<String> getObjects(FilterCriteria filterCriteria) {
        List<String[]> samplingResults = characterisationResultGateway.getSamples(filterCriteria, algorithm, properties);
        List<String> results = new ArrayList<>();
        switch (algorithm) {

            case RANDOM -> {
                results.addAll(samplingResults.stream().map(item -> item[1]).collect(Collectors.toList()));
            }
            case SELECTIVE_FEATURE_DISTRIBUTION -> {
                results.addAll(samplingResults.stream().map(item -> item[1]).collect(Collectors.toList()));
            }
        }
        return results;
    }

    @Override
    public List<String[]> getSamplingInfo(FilterCriteria filterCriteria) {
        List<String[]> samplingResults = characterisationResultGateway.getSamples(filterCriteria, algorithm, properties);

        return samplingResults;
    }


}
