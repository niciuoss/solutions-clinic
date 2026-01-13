package com.jettech.api.solutions_clinic.model.usecase.room;

import com.jettech.api.solutions_clinic.model.entity.Room;
import com.jettech.api.solutions_clinic.model.repository.RoomRepository;
import com.jettech.api.solutions_clinic.model.repository.TenantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultGetRoomsByTenantUseCase implements GetRoomsByTenantUseCase {

    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> execute(GetRoomsByTenantRequest request) throws AuthenticationException {
        // Validar se o tenant existe
        tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada com ID: " + request.tenantId()));

        // Buscar salas por tenant
        List<Room> rooms;
        if (request.activeOnly()) {
            rooms = roomRepository.findByTenantIdAndIsActive(request.tenantId(), true);
        } else {
            rooms = roomRepository.findByTenantId(request.tenantId());
        }

        // Converter para List<RoomResponse>
        return rooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getTenant().getId(),
                room.getName(),
                room.getDescription(),
                room.getCapacity(),
                room.isActive(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }
}
