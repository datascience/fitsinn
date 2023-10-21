package rocks.artur.api;

import java.io.File;

/**
 * This interface enables the following actions:
 * - to analyze a digital object using a characterisation tool,
 * - to persist a characterisation result in a db.
 */
public interface AnalyzePersistFile {
    Long uploadCharacterisationResults(File file);
    Long uploadCharacterisationResults(byte[] file, String filename);
}
