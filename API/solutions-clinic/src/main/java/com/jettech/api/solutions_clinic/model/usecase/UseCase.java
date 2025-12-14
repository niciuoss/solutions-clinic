package com.jettech.api.solutions_clinic.model.usecase;

import javax.naming.AuthenticationException;

public interface UseCase<IN, OUT> {

    OUT execute(IN in) throws AuthenticationException;

}
