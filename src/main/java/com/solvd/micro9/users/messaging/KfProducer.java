package com.solvd.micro9.users.messaging;

import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.es.Es;

public interface KfProducer {

    void send(String key, Es value);

    void send(String key, User value);

}
