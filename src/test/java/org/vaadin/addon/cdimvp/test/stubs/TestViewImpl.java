package org.vaadin.addon.cdimvp.test.stubs;

import javax.annotation.PostConstruct;

import org.vaadin.addon.cdimvp.AbstractMVPView;

import com.vaadin.ui.VerticalLayout;

public class TestViewImpl extends AbstractMVPView implements TestView {

    private final VerticalLayout mainLayout = new VerticalLayout();
    private boolean entered;
    private boolean eventHandled;

    @PostConstruct
    public void init() {
        setCompositionRoot(mainLayout);
    }

    @Override
    public void changeSomethingOnFirstEnter() {
        entered = true;
    }

    public boolean isEntered() {
        return entered;
    }

    public void fireAnEvent() {
        fireViewEvent(TestPresenter.TEST_METHOD, 1, 2, 3);
    }

    @Override
    public void changeSomethingOnEvent() {
        eventHandled = true;
    }

    public boolean isEventHandled() {
        return eventHandled;
    }

}
