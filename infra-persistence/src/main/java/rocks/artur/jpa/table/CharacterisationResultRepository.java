package rocks.artur.jpa.table;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CharacterisationResultRepository extends JpaRepository<CharacterisationResultJPA, String>, CustomCharacterisationResultRepository {

    @Query("select property, count(*) as count from CharacterisationResultJPA group by property")
    List<Object[]> getPropertyDistribution();

    @Query("select distinct filePath, property from CharacterisationResultJPA")
    List<Object[]> getFilepathProperty();

    List<CharacterisationResultJPA> findAllByFilePath(String filePath);


}
