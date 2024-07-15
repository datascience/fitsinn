package rocks.artur.api;

import rocks.artur.api_impl.utils.ByteFile;

import java.util.List;

/**
 * This interface enables the following actions:
 * - to analyze a digital object using a characterisation tool,
 * - to persist a characterisation result in a db.
 */
public interface AnalyzePersistFile {

    Long uploadCharacterisationResults(ByteFile file, String datasetName);

    Long uploadCharacterisationResults(List<ByteFile> files, String datasetName);

}
