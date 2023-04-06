package com.solvd.micro9.users.service;

import com.solvd.micro9.users.domain.es.Es;

public interface DbSynchronizer {

    void sync(Es event);

}
