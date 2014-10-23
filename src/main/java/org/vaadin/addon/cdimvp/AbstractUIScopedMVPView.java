package org.vaadin.addon.cdimvp;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
@UIScopedStereotype
public abstract class AbstractUIScopedMVPView extends ViewComponent implements MVPView {
    protected transient Logger logger = Logger.getLogger(getClass().getName());

    protected Class<? extends MVPView> viewInterface;

    /**
     * Called (by the application logic) whenever the view is entered.
     */
    @SuppressWarnings("unchecked")
    public void enter() {
        if (viewInterface == null) {
            // Determine the view interface
            for (final Class<?> clazz : AbstractUIScopedMVPView.this.getClass()
                    .getInterfaces()) {
                if (!clazz.equals(MVPView.class)
                        && MVPView.class.isAssignableFrom(clazz)) {
                    viewInterface = (Class<? extends MVPView>) clazz;
                }
            }
        }
        
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "View fireViewEvent : {0}", 
                viewInterface.getName() + AbstractUIScopedMVPPresenter.VIEW_ENTER);
        }            
        fireViewEvent(
                viewInterface.getName() + AbstractUIScopedMVPPresenter.VIEW_ENTER, this);
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.INFO, "View accessed: {0}", viewInterface);
        }
    }
}
