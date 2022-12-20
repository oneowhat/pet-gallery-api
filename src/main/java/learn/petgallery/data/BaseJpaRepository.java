package learn.petgallery.data;

import learn.petgallery.models.AuditableEntity;
import learn.petgallery.models.EntityEvent;
import learn.petgallery.models.EntityEventType;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class BaseJpaRepository<T extends AuditableEntity, ID> extends SimpleJpaRepository<T, ID> {

    private final EntityManager entityManager;

    BaseJpaRepository(JpaEntityInformation entityInformation,
                     EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Transactional
    public T save(T entity, int appUserId) {
        EntityEventType entityEventType = entity.getId() == 0
                ? EntityEventType.ADD
                : EntityEventType.UPDATE;
        T t = super.save(entity);
        entityManager.persist(EntityEvent.createSaveEvent(entity, entityEventType, appUserId));
        return t;
    }

    @Transactional
    public void deleteById(ID entityId, int appUserId) {
        super.deleteById(entityId);
        Type mySuperclass = getClass().getGenericSuperclass();
        Type tType = ((ParameterizedType)mySuperclass).getActualTypeArguments()[0];
        entityManager.persist(EntityEvent.createDeleteEvent((int) entityId, tType.getTypeName(), appUserId));
    }
}
