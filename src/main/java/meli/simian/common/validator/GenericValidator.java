package meli.simian.common.validator;

import java.util.Set;

public interface GenericValidator<T> {
    Set<String> checkForErrors(T object);

    void validate(T object);
}
