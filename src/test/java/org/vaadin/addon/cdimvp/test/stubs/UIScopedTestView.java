package org.vaadin.addon.cdimvp.test.stubs;

import org.vaadin.addon.cdimvp.MVPView;

public interface UIScopedTestView extends MVPView {

    void changeSomethingOnFirstEnter();

    void changeSomethingOnEvent();

}
