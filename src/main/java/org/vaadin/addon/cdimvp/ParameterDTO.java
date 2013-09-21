package org.vaadin.addon.cdimvp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Parameter DTO for CDI Events.
 */
@SuppressWarnings("unchecked")
public class ParameterDTO {
    private final Object primaryParameter;

    private final Object[] secondaryParameters;

    public ParameterDTO(final Object primaryParameter,
            final Object... secondaryParameters) {
        super();
        this.primaryParameter = primaryParameter;
        this.secondaryParameters = secondaryParameters;
    }

    public Object getPrimaryParameter() {
        return primaryParameter;
    }

    public Object[] getSecondaryParameters() {
        return secondaryParameters;
    }

    public <T extends Object> T getPrimaryParameter(final Class<T> clazz) {
        return (T) getPrimaryParameter();
    }

    public <T extends Object> T getSecondaryParameter(final int index,
            final Class<T> clazz) {
        T parameter = null;
        if (secondaryParameters != null && index < secondaryParameters.length) {
            parameter = (T) secondaryParameters[index];
        }
        return parameter;
    }

    public <T extends Object> List<T> getSecondaryParametersList(
            final Class<T> clazz) {
        List<T> parameters = Collections.emptyList();
        if (secondaryParameters != null) {
            parameters = (List<T>) Arrays.asList(secondaryParameters);
        }
        return parameters;
    }
}
