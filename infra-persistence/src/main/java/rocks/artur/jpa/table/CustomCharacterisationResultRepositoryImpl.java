package rocks.artur.jpa.table;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rocks.artur.jpa.view.FilterJPA;

import java.util.List;

@Repository
public class CustomCharacterisationResultRepositoryImpl implements CustomCharacterisationResultRepository {
    private static final Logger LOG = LoggerFactory.getLogger(CustomCharacterisationResultRepositoryImpl.class);
    private final EntityManager entityManager;
    private final FilterJPA filterJPA = new FilterJPA();

    public CustomCharacterisationResultRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    @Transactional
    public void saveFast(List<CharacterisationResultJPA> results) {

        for (CharacterisationResultJPA result : results) {
            CharacterisationResultID id = new CharacterisationResultID();
            id.setProperty(result.getProperty());
            id.setSource(result.getSource());
            id.setFilePath(result.getFilePath());
            CharacterisationResultJPA found = entityManager.find(CharacterisationResultJPA.class, id);
            if (found == null) {
                entityManager.persist(result);
            } else {
                entityManager.merge(result);
            }

        }
    }
}
