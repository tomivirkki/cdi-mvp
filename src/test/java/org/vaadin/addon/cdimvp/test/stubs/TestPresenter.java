package org.vaadin.addon.cdimvp.test.stubs;

import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;

@ViewInterface(TestView.class)
public class TestPresenter extends AbstractMVPPresenter<TestView> {

    public static final String TEST_METHOD = "TestPresenter.TEST_METHOD";

    private boolean viewEntered;
    private ParameterDTO eventParameters;

    @Override
    public void viewEntered() {
        if (!viewEntered) {
            view.changeSomethingOnFirstEnter();
        }
        viewEntered = true;
    }

    public TestView getView() {
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
