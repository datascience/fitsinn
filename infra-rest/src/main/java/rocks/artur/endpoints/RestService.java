package rocks.artur.endpoints;


import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rocks.artur.api.*;
import rocks.artur.api_impl.utils.ByteFile;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;
import rocks.artur.domain.SamplingAlgorithms;
import rocks.artur.domain.statistics.PropertiesPerObjectStatistic;
import rocks.artur.domain.statistics.PropertyStatistic;
import rocks.artur.domain.statistics.PropertyValueStatistic;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@CrossOrigin
@RestController
public class RestService {
    private static final Logger LOG = LoggerFactory.getLogger(RestService.class);
    GetSources getSources;
    GetSamples getSamples;
    GetObjects getObjects;
    GetProperties getProperties;
    GetPropertyValueDistribution getPropertyValueDistribution;
    AnalyzePersistFile analyzePersistFile;
    GetCollectionStatistics getCollectionStatistics;
    GetDatasetInfo getDatasetInfo;

    ResolveConflicts resolveConflicts;

    public RestService(GetProperties getProperties,
                       GetPropertyValueDistribution getPropertyValueDistribution,
                       AnalyzePersistFile analyzePersistFile,
                       GetObjects getObjects, GetCollectionStatistics getCollectionStatistics,
                       GetSources getSources, GetSamples getSamples, ResolveConflicts resolveConflicts, GetDatasetInfo getDatasetInfo) {
        this.getProperties = getProperties;
        this.getObjects = getObjects;
        this.getPropertyValueDistribution = getPropertyValueDistribution;
        this.analyzePersistFile = analyzePersistFile;
        this.getCollectionStatistics = getCollectionStatistics;
        this.getSources = getSources;
        this.getSamples = getSamples;
        this.resolveConflicts = resolveConflicts;
        this.getDatasetInfo = getDatasetInfo;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/health")
    public String getHealth() {
        return "OK";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/sources")
    public List<String> getSources(
            @RequestParam(name = "datasetName", required = true, defaultValue = "default") @Parameter(name = "datasetName", description = "dataset name", example = "default") String datasetName) {
        List<String> sources = getSources.getSources(datasetName);
        return sources;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/operators")
    public String[] getOperators() {
        String[] simpleOperationSet = SearchOperation.SIMPLE_OPERATION_SET;
        return simpleOperationSet;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/properties")
    public List<PropertyStatistic> getProperties(@RequestParam(name = "filter", required = false) @Parameter(name = "filter", description = "Filter", example = "FORMAT=\"Portable Document Format\"") String filter,
                                                 @RequestParam(name = "datasetName", required = true, defaultValue = "default") @Parameter(name = "datasetName", description = "dataset name", example = "default") String datasetName) throws ParseException {
        CriteriaParser parser = new CriteriaParser();
        FilterCriteria filterCriteria = parser.parse(filter);
        List<PropertyStatistic> propertyDistribution = getProperties.getProperties(filterCriteria, datasetName);
        return propertyDistribution;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/object")
    @Consumes(MediaType.APPLICATION_JSON)
    public Iterable<CharacterisationResult> getObject(
            @RequestParam(name = "filepath", required = true) @Parameter(name = "filepath", description = "Filepath of a digital object", example = "/home/user/file1") String filepath,
            @RequestParam(name = "datasetName", required = true, defaultValue = "default") @Parameter(name = "datasetName", description = "dataset name", example = "default") String datasetName) {
        Iterable<CharacterisationResult> objects = getObjects.getObject(filepath, datasetName);
        return objects;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/objectconflicts")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Property> getConflictsPerObject(
            @RequestParam(name = "filepath", required = true) @Parameter(name = "filepath", description = "Filepath of a digital object", example = "/home/user/file1") String filepath,
            @RequestParam(name = "datasetName", required = true, defaultValue = "default") @Parameter(name = "datasetName", description = "dataset name", example = "default") String datasetName) {
        List<CharacterisationResult> objects = getObjects.getConflictsFromObject(filepath, datasetName);
        List<Property> collect = objects.stream().map(item -> item.getProperty()).collect(Collectors.toList());
        return collect;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/statistics")
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Double> getCollectionStatistics(@RequestParam(name = "filter", required = false) @Parameter(name = "filter", description = "Filter", example = "FORMAT=\"Portable Document Format\"") String filter,
                                                       @RequestParam(name = "datasetName", required = true, defaultValue = "default") @Parameter(name = "datasetName", description = "dataset name", example = "default") String datasetName) throws ParseException {
        CriteriaParser parser = new CriteriaParser();
        FilterCriteria filterCriteria = parser.parse(filter);
        Map<String, Double> sizeStatistics = getCollectionStatistics.getStatistics(filterCriteria, datasetName);
        return sizeStatistics;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/objects")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<PropertiesPerObjectStatistic> getObjects(@RequestParam(name = "filter", required = false) @Parameter(name = "filter", description = "Filter", example = "FORMAT=\"Portable Document Format\"") String filter,
                                                         @RequestParam(name = "datasetName", required = true, defaultValue = "default") @Parameter(name = "datasetName", description = "dataset name", example = "default") String datasetName) throws ParseException {

        CriteriaParser parser = new CriteriaParser();
        FilterCriteria filterCriteria = parser.parse(filter);
        List<PropertiesPerObjectStatistic> objects = getObjects.getObjects(filterCriteria, datasetName);
        return objects;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/propertyvalues")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<PropertyValueStatistic> getPropertyValueDistribution(
            @RequestParam(name = "property", required = true) @Parameter(name = "property", description = "Property of a digital object", example = "FORMAT") Property property,
            @RequestParam(name = "filter", required = false) @Parameter(name = "filter", description = "Filter", example = "FORMAT=\"Portable Document Format\"") String filter,
            @RequestParam(name = "datasetName", required = true, defaultValue = "default") @Parameter(name = "datasetName", description = "dataset name", example = "default") String datasetName) throws ParseException {

        LOG.debug("filter: " + filter);

        CriteriaParser parser = new CriteriaParser();
        FilterCriteria filterCriteria = parser.parse(filter);

        List<PropertyValueStatistic> valueDistributionByProperty =
                getPropertyValueDistribution.getPropertyValueDistribution(property, filterCriteria, datasetName);


        return valueDistributionByProperty;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/samples")
    @Consumes(MediaType.APPLICATION_JSON)
    public Iterable<String> getSamples(
            @RequestParam(name = "algorithm", required = true) @Parameter(name = "algorithm", description = "Sampling algorithm", example = "RANDOM") SamplingAlgorithms algorithm,
            @RequestParam(name = "properties", required = false) @Parameter(name = "properties", description = "A list of properties") List<Property> properties,
            @RequestParam(name = "filter", required = false) @Parameter(name = "filter", description = "Filter", example = "FORMAT=\"Portable Document Format\"") String filter,
            @RequestParam(name = "datasetName", required = true, defaultValue = "default") @Parameter(name = "datasetName", description = "dataset name", example = "default") String datasetName) throws ParseException {

        CriteriaParser parser = new CriteriaParser();
        FilterCriteria filterCriteria = parser.parse(filter);

        getSamples.setAlgorithm(algorithm);
        getSamples.setProperties(properties);

        Iterable<String> objects = getSamples.getObjects(filterCriteria, datasetName);
        return objects;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/samplinginfo")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<String[]> getSamplingInfo(
            @RequestParam(name = "algorithm", required = true) @Parameter(name = "algorithm", description = "Sampling algorithm", example = "RANDOM") SamplingAlgorithms algorithm,
            @RequestParam(name = "properties", required = false) @Parameter(name = "properties", description = "A list of properties") List<Property> properties,
            @RequestParam(name = "filter", required = false) @Parameter(name = "filter", description = "Filter", example = "FORMAT=\"Portable Document Format\"") String filter,
            @RequestParam(name = "datasetName", required = true, defaultValue = "default") @Parameter(name = "datasetName", description = "dataset name", example = "default") String datasetName) throws ParseException {

        CriteriaParser parser = new CriteriaParser();
        FilterCriteria filterCriteria = parser.parse(filter);

        getSamples.setAlgorithm(algorithm);
        getSamples.setProperties(properties);

        List<String[]> samplingInfo = getSamples.getSamplingInfo(filterCriteria, datasetName);
        return samplingInfo;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/upload", consumes = {
            "multipart/form-data"})
    public Response ProcessFile(
            @RequestParam(name = "file", required = true) @Parameter(name = "file", description = "Please select a digital object to upload") MultipartFile file,
            @RequestParam(name = "datasetName", required = true, defaultValue = "default") @Parameter(name = "datasetName", description = "dataset name", example = "default") String datasetName) throws IOException {
        String filename = file.getOriginalFilename();
        byte[] bytes = file.getBytes();
        LOG.debug(String.format("Processing file { %s }", file.getOriginalFilename()));
        ByteFile byteFile = new ByteFile(bytes, filename);
        Long totalCount = analyzePersistFile.uploadCharacterisationResults(byteFile, datasetName);

        Response response =
                Response.ok(totalCount).build();

        return response;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/multipleupload", consumes = {
            "multipart/form-data"})
    public Response ProcessFiles(@RequestPart(name = "files", required = true) @Parameter(name = "files", description = "A list of digital objects to upload") MultipartFile[] files,
                                 @RequestParam(name = "datasetName", required = true, defaultValue = "default") @Parameter(name = "datasetName", description = "dataset name", example = "default") String datasetName) throws IOException {
        Long totalCount = 0L;
        List<ByteFile> byteFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            LOG.debug(String.format("Processing file { %s }", file.getOriginalFilename()));
            ByteFile byteFile = new ByteFile(file.getBytes(), file.getOriginalFilename());
            byteFiles.add(byteFile);
        }
        analyzePersistFile.uploadCharacterisationResults(byteFiles, datasetName);
        Response response = Response.ok(totalCount).build();
        return response;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/resolveconflicts")
    @Consumes(MediaType.APPLICATION_JSON)
    public void resolveConflicts(@RequestParam(name = "datasetName", required = true, defaultValue = "default") @Parameter(name = "datasetName", description = "dataset name", example = "default") String datasetName) throws ParseException {
        resolveConflicts.run(datasetName);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/datasets")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<String> listDatasets() {
        return getDatasetInfo.listDatasets();
    }
}
