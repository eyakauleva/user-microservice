package com.solvd.micro9.users.persistence.eventstore.listener;

import com.solvd.micro9.users.domain.es.EsUser;
import com.solvd.micro9.users.domain.exception.ServerException;
import com.solvd.micro9.users.service.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class EsUserListener extends AbstractMongoEventListener<EsUser> {

    private final SequenceGeneratorService sequenceGenerator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<EsUser> event) {
        try {
            if (Objects.isNull(event.getSource().getId())) {
                event.getSource().setId(
                        sequenceGenerator.generateSequence(EsUser.SEQUENCE_NAME)
                );
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ServerException(e);
        }
    }

}

