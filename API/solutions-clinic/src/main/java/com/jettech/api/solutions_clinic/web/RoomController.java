package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.room.CreateRoomRequest;
import com.jettech.api.solutions_clinic.model.usecase.room.DefaultCreateRoomUseCase;
import com.jettech.api.solutions_clinic.model.usecase.room.DefaultGetRoomByIdUseCase;
import com.jettech.api.solutions_clinic.model.usecase.room.RoomResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RoomController implements RoomAPI {

    private final DefaultCreateRoomUseCase createRoomUseCase;
    private final DefaultGetRoomByIdUseCase getRoomByIdUseCase;

    @Override
    public RoomResponse createRoom(@Valid @RequestBody CreateRoomRequest request) throws AuthenticationException {
        return createRoomUseCase.execute(request);
    }

    @Override
    public RoomResponse getRoomById(@PathVariable UUID id) throws AuthenticationException {
        return getRoomByIdUseCase.execute(id);
    }
}

