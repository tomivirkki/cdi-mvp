package org.vaadin.addon.cdimvp.test.stubs;

import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;

import com.vaadin.cdi.ViewScoped;

@ViewScoped
@ViewInterface(ViewScopedTestView.class)
public class ViewScopedTestPresenter extends
        AbstractMVPPresenter<ViewScopedTestView> {

    public static final String TEST_METHOD = "ViewScopedTestPresenter.TEST_METHOD";

    private boolean viewEntered;
    private ParameterDTO eventParameters;

    @Override
    public void viewEntered() {
        if (!viewEntered) {
            view.changeSomethingOnFirstEnter();
        }
        viewEntered = true;
    }

    public ViewScopedTestView getView() {
        return view;
    }

    public boolean isViewEntered() {
        return viewEntered;
    }

    public ParameterDTO getEventParameters() {
        return eventParameters;
    }

    public void reactToEvent(@Observes @CDIEvent(TEST_METHOD) ParameterDTO dto) {
        eventParameters = dto;
        view.changeSomethingOnEvent();
    }
}
