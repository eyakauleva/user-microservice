package com.solvd.micro9.users.web.mapper;

import com.solvd.micro9.users.domain.es.EsUser;
import com.solvd.micro9.users.web.dto.EsDto;
import org.mapstruct.Mapper;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public interface EsMapper {

    EsDto domainToDto(EsUser esUser);

    default Mono<EsDto> domainToDto(Mono<EsUser> esUserMono) {
        return esUserMono.map(this::domainToDto);
    }

}
