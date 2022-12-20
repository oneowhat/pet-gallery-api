package learn.petgallery.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
public class EntityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int entityEventId;

    private int entityId;
    private String entityName;
    private String entityBody;
    @NotNull
    private EntityEventType entityEventType;
    private int appUserId;

    private EntityEvent(AuditableEntity entity, EntityEventType entityEventType, int appUserId) {
        this.entityId = entity.getId();
        this.entityName = entity.getClass().getName();
        this.entityBody = entity.toString();
        this.entityEventType = entityEventType;
        this.appUserId = appUserId;
    }

    public static EntityEvent createSaveEvent(AuditableEntity entity, EntityEventType entityEventType, int appUserId) {
        return new EntityEvent(entity, entityEventType, appUserId);
    }

    public static EntityEvent createDeleteEvent(int entityId, String entityName, int appUserId) {
        EntityEvent entityEvent = new EntityEvent();
        entityEvent.appUserId = appUserId;
        entityEvent.entityName = entityName;
        entityEvent.entityEventType = EntityEventType.DELETE;
        entityEvent.entityId = entityId;
        return entityEvent;
    }
}
