package org.vaadin.addon.cdimvp;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Abstract CDI MVP presenter. Associated {@link MVPView} interface extension is
 * declared for each extended {@link AbstractUIScopedMVPPresenter} using the
 * {@link ViewInterface} annotation. This is for use of with UIScoped MVPViews 
 */
@SuppressWarnings("serial")
@UIScopedStereotype
public abstract class AbstractUIScopedMVPPresenter<T extends MVPView> implements
        Serializable, MVPPresenter {
    private transient Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    private Instance<MVPView> viewInstance;

    protected T view;

    public static final String VIEW_ENTER = "AbstractUIScopedPresenter_ve";

    @SuppressWarnings("unchecked")
    @PostConstruct
    protected void postConstruct() {
        // ViewInterface must be defined
        final Class<? extends MVPView> viewInterface = getClass()
                .getAnnotation(ViewInterface.class).value();
        view = (T) viewInstance.select(viewInterface).get();

        logger.info("Presenter initialized: " + getClass());
    }

    /**
     * Performs view actions called each time the view is entered.
     */
    public abstract void viewEntered();

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public static @interface ViewInterface {
        Class<? extends MVPView> value();
    }
}
