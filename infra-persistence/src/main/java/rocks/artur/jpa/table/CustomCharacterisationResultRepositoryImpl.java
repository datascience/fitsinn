package rocks.artur.jpa.table;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
public class CustomCharacterisationResultRepositoryImpl implements CustomCharacterisationResultRepository {
    private static final Logger LOG = LoggerFactory.getLogger(CustomCharacterisationResultRepositoryImpl.class);
    private final EntityManager entityManager;

    public CustomCharacterisationResultRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    @Transactional
    public void saveFast(Collection<CharacterisationResultJPA> results) {

        for (CharacterisationResultJPA result : results) {
            entityManager.persist(result);
        }
    }
}
