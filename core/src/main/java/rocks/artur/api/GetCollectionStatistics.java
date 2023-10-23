package rocks.artur.api;

import java.util.Map;

public interface GetCollectionStatistics {
    Map<String, Object> getSizeStatistics();

    double getConflictRate();
}
