package com.solvd.micro9.users.domain.es;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "es_users")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class EsUser extends Es {

    @Transient
    public static final String SEQUENCE_NAME = "event_store_users_sequence";

    @Builder
    public EsUser(Long id,
                  EsType type,
                  LocalDateTime time,
                  String createdBy,
                  String entityId,
                  String payload,
                  EsStatus status) {
        super(id, type, time, createdBy, entityId, payload, status);
    }

}
