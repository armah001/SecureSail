package com.example.amalisecuresail.mapper;

import com.example.amalisecuresail.dto.UserDto;
import com.example.amalisecuresail.dto.UserProfileDto;
import com.example.amalisecuresail.entity.User;
import com.example.amalisecuresail.payload.UpdateProfileRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


/**
 * Mapper interface for converting between {@link User} entities and {@link UserDto} DTOs.
 */
@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "password", ignore = true)
    })
    void updateUserFromDto(UserDto dto, @MappingTarget User user);

    UserDto toDto(User user);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "password", ignore = true)
    })
    void updateProfileFromDto(UpdateProfileRequest dto, @MappingTarget User user);

}

