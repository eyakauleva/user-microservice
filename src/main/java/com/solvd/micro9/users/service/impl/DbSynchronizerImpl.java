package com.solvd.micro9.users.service.impl;

import com.solvd.micro9.users.domain.es.Es;
import com.solvd.micro9.users.service.DbSynchronizer;
import com.solvd.micro9.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class DbSynchronizerImpl implements DbSynchronizer {

    private final UserService userService;

    @Override
    public void sync(final Es event) {
        switch (event.getType()) {
            case USER_CREATED:
                userService.create(event)
                        .subscribeOn(Schedulers.boundedElastic())
                        .subscribe();
                break;
            case USER_DELETED:
                userService.delete(event)
                        .subscribeOn(Schedulers.boundedElastic())
                        .subscribe();
                break;
            default:
                break;
        }
    }

}
