package rocks.artur.jpa.table;

import java.util.Collection;

public interface CustomCharacterisationResultRepository {

    void saveFast(Collection<CharacterisationResultJPA> results);


}
