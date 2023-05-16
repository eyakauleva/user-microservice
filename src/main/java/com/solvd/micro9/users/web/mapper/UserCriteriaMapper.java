package com.solvd.micro9.users.web.mapper;

import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.web.dto.criteria.UserCriteriaDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserCriteriaMapper {

    UserCriteria dtoToDomain(UserCriteriaDto criteriaDto);

}
