package rocks.artur.api;

import java.util.List;

/**
 * This interface enables getting a property distribution.
 */
public interface GetSources {
    List<String> getSources(String datasetName);
}
