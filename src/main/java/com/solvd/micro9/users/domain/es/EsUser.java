package com.solvd.micro9.users.domain.es;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "es_users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EsUser extends Es {

    @Transient
    public static final String SEQUENCE_NAME = "event_store_users_sequence";

    @Builder
    public EsUser(final Long id,
                  final EsType type,
                  final LocalDateTime time,
                  final String createdBy,
                  final String entityId,
                  final String payload,
                  final EsStatus status) {
        super(id, type, time, createdBy, entityId, payload, status);
    }

}
