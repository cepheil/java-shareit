package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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

//@NoArgsConstructor(access = AccessLevel.PRIVATE)
//public class UserMapper {
//
//    public static UserDto toUserDto(User user) {
//        if (user == null) {
//            return null;
//        }
//        return new UserDto(
//                user.getId(),
//                user.getName(),
//                user.getEmail()
//        );
//    }
//
//    public static User toUser(UserCreateDto userDto) {
//        if (userDto == null) {
//            return null;
//        }
//        User user = new User();
//        user.setName(userDto.getName());
//        user.setEmail(userDto.getEmail());
//        return user;
//    }
//
//    public static void updateUser(User user, UserUpdateDto dto) {
//        if (dto == null) {
//            return;
//        }
//        if (dto.getName() != null) {
//            user.setName(dto.getName());
//        }
//        if (dto.getEmail() != null) {
//            user.setEmail(dto.getEmail());
//        }
//    }
//
//}
