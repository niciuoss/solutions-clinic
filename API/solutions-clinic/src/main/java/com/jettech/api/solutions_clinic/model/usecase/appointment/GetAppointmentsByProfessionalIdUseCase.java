package com.jettech.api.solutions_clinic.model.usecase.appointment;

import com.jettech.api.solutions_clinic.model.usecase.UseCase;

import java.util.List;
import java.util.UUID;

public interface GetAppointmentsByProfessionalIdUseCase extends UseCase<UUID, List<AppointmentResponse>> {
}
