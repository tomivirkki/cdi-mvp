package org.vaadin.addon.cdimvp.test.stubs;

import javax.annotation.PostConstruct;

import org.vaadin.addon.cdimvp.AbstractMVPView;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.ViewScoped;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

@CDIView("viewscopedtestview")
@ViewScoped
public class ViewScopedTestViewImpl extends AbstractMVPView implements
        ViewScopedTestView, View {

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
        fireViewEvent(ViewScopedTestPresenter.TEST_METHOD, 1, 2, 3);
    }

    @Override
    public void changeSomethingOnEvent() {
        eventHandled = true;
    }

    public boolean isEventHandled() {
        return eventHandled;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        enter();
    }

}
