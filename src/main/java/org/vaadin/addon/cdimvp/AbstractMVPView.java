package org.vaadin.addon.cdimvp;

import java.util.logging.Logger;

import com.vaadin.cdi.UIScoped;

@SuppressWarnings("serial")
@UIScoped
public abstract class AbstractMVPView extends ViewComponent implements MVPView {
    protected transient Logger logger = Logger.getLogger(getClass().getName());

    protected Class<? extends MVPView> viewInterface;

    /**
     * Called (by the application logic) whenever the view is entered.
     */
    public void enter() {
        if (viewInterface == null) {
            // Determine the view interface
            for (final Class<?> clazz : AbstractMVPView.this.getClass()
                    .getInterfaces()) {
                if (!clazz.equals(MVPView.class)
                        && MVPView.class.isAssignableFrom(clazz)) {
                    viewInterface = (Class<? extends MVPView>) clazz;
                }
            }
        }

        fireViewEvent(
                viewInterface.getName() + AbstractMVPPresenter.VIEW_ENTER, this);
        logger.info("View accessed: " + viewInterface);
    }

}
