package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.room.CreateRoomRequest;
import com.jettech.api.solutions_clinic.model.usecase.room.DefaultCreateRoomUseCase;
import com.jettech.api.solutions_clinic.model.usecase.room.DefaultGetRoomByIdUseCase;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RoomController implements RoomAPI {

    private final DefaultCreateRoomUseCase createRoomUseCase;
    private final DefaultGetRoomByIdUseCase getRoomByIdUseCase;

    @Override
    public ResponseEntity<Object> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        try {
            var result = this.createRoomUseCase.execute(request);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar sala: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> getRoomById(@PathVariable UUID id) {
        try {
            var result = getRoomByIdUseCase.execute(id);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar sala: " + e.getMessage());
        }
    }
}

