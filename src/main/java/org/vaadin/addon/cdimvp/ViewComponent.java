package org.vaadin.addon.cdimvp;

import javax.inject.Inject;

import org.vaadin.addon.cdimvp.CDIEvent.CDIEventImpl;

import com.vaadin.ui.CustomComponent;

/**
 * Superclass for concrete Views and their subcomponents.
 */
@SuppressWarnings("serial")
public abstract class ViewComponent extends CustomComponent {
    @Inject
    private javax.enterprise.event.Event<ParameterDTO> viewEvent;

    protected void fireViewEvent(final String methodIdentifier,
            final Object primaryParameter, final Object... secondaryParameters) {
        viewEvent.select(new CDIEventImpl(methodIdentifier)).fire(
                new ParameterDTO(primaryParameter, secondaryParameters));
    }
}
