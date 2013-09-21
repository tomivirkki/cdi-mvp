package org.vaadin.addon.cdimvp;

import java.util.logging.Logger;

import com.vaadin.cdi.UIScoped;

@SuppressWarnings("serial")
@UIScoped
public abstract class AbstractView extends ViewComponent implements View {
    protected transient Logger logger = Logger.getLogger(getClass().getName());

    protected Class<? extends View> viewInterface;

    @Override
    public void enter() {
        if (viewInterface == null) {
            // Determine the view interface
            for (final Class<?> clazz : AbstractView.this.getClass()
                    .getInterfaces()) {
                if (!clazz.equals(View.class)
                        && View.class.isAssignableFrom(clazz)) {
                    viewInterface = (Class<? extends View>) clazz;
                }
            }
        }

        fireViewEvent(viewInterface.getName() + AbstractPresenter.VIEW_ENTER,
                this);
        logger.info("View accessed: " + viewInterface);
    }

}
