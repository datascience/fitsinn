package rocks.artur.jpa.table;

import java.util.List;

public interface CustomCharacterisationResultRepository {

    void saveFast(List<CharacterisationResultJPA> results);


}
