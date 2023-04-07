package com.solvd.micro9.users.domain.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Es (event store) is a base class for event sourcing events
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Es {

    @Id
    private Long id;

    private EsType type;

    private LocalDateTime time;

    @Field(name = "created_by")
    private String createdBy;

    @Field(name = "entity_id")
    private String entityId;

    private String payload;

    private EsStatus status;

}
