package com.solvd.micro9.users.service;

import java.util.concurrent.ExecutionException;

public interface SequenceGeneratorService {

    Long generateSequence(final String sequenceName) throws InterruptedException, ExecutionException;

}
