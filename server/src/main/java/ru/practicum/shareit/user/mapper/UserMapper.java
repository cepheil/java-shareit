package ru.practicum.shareit.user.mapper;


import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;


import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface UserMapper {

    // User -> UserDto
    UserDto toUserDto(User user);

    // UserCreateDto -> User
    User toUser(UserCreateDto dto);

    // Обновление сущности User частично из UserUpdateDto
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateDto dto);
}
