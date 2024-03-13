package rocks.artur.jpa.view;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rocks.artur.jpa.table.CharacterisationResultJPA;

import java.util.List;

@Repository
public interface CharacterisationResultViewRepository extends JpaRepository<CharacterisationResultViewJPA,
        CharacterisationResultViewID>, CustomCharacterisationResultViewRepository {
    List<CharacterisationResultViewJPA> findByFilePath(String filePath);

    @Query("select property, count(*) as count from CharacterisationResultViewJPA group by property" )
    List<Object[]> getPropertyDistribution();

    @Query("select count(*) as count from CharacterisationResultViewJPA where value='CONFLICT'")
    Long getConflictCount();


    @Query("select distinct filePath, property from CharacterisationResultViewJPA where value='CONFLICT'")
    List<String[]> getConflictEntries();

    List<CharacterisationResultViewJPA> findAllByFilePath(String filePath);



    @Query("select sum(cast (value as int)) as totalsize from CharacterisationResultViewJPA where property='SIZE'")
        // @Query("select sum(cast (value as int)) as sum from CharacterisationResultJPA where property='SIZE'  ")
    Long getTotalSize();

    @Query("select min(cast (value as int)) as totalsize from CharacterisationResultViewJPA where property='SIZE'")
    Long getMinSize();

    @Query("select max(cast (value as int)) as totalsize from CharacterisationResultViewJPA where property='SIZE'")
    Long getMaxSize();

    @Query("select avg(cast (value as int)) as totalsize from CharacterisationResultViewJPA where property='SIZE'")
    Long getAvgSize();

    @Query("select count(distinct filePath) from CharacterisationResultJPA")
    Long getTotalCount();


    @Query("select distinct source from CharacterisationResultJPA")
    List<String> getSources();
}