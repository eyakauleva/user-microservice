ARG ELASTIC_VERSION

# https://www.docker.elastic.co/
FROM logstash:${ELASTIC_VERSION}

# Add your logstash plugins setup here
# Example: RUN logstash-plugin install logstash-filter-json

RUN bin/logstash-plugin install logstash-input-mongodb

USER root

RUN cd /opt \
    && mkdir logstash-mongodb \
    && chown logstash:logstash /opt/logstash-mongodb/

USER logstash
