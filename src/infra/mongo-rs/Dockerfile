FROM mongo:5.0.1

RUN echo "secretpasswordkey" > "/tmp/replica.key"
RUN chmod 600 /tmp/replica.key
RUN chown 999:999 /tmp/replica.key

CMD ["mongod", "--replSet", "rs0", "--bind_ip_all", "--keyFile", "/tmp/replica.key"]