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

    @Query("select property, count(*) as count from CharacterisationResultViewJPA group by property")
    List<Object[]> getPropertyDistribution();

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

    @Query( "select case " +
            "           when cast(value as int) between 0 and 1000 then '0-1KB' " +
            "           when cast(value as int) between 1000 and 1000000 then '1KB-1MB' " +
            "           when cast(value as int) between 1000000 and 10000000 then '1MB-10MB' " +
            "           when cast(value as int) between 10000000 and 100000000 then '10MB-100MB' " +
            "           when cast(value as int) between 100000000 and 1000000000 then '100MB-1GB' " +
            "           when cast(value as int) between 1000000000 and 10000000000 then '1GB-10GB' " +
            "           else '10GB+' end as size_band, " +
            "       count(*) " +
            "from CharacterisationResultViewJPA " +
            "where property = 'SIZE' " +
            "group by 1")
    List<Object[]> getSizeDistribution();




    @Query("select distinct source from CharacterisationResultJPA")
    List<String> getSources();
}