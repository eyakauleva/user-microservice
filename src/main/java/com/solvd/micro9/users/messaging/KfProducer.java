package com.solvd.micro9.users.messaging;

public interface KfProducer<K, X> {

    void send(K key, X value);

}
