package org.vaadin.addon.cdimvp;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ObserverMethod;

import org.vaadin.addon.cdimvp.CDIEvent.CDIEventImpl;

@SuppressWarnings("serial")
public class MVPExtension implements Extension, Serializable {

    private static final Logger LOGGER = Logger.getLogger(MVPExtension.class.getName());
    private java.util.Set<Bean<?>> presenterBeans
            = new java.util.HashSet<Bean<?>>();

    /**
     * Cdi 1.1 , It is safe to call getBeans on AfterDeploymentValidation
     */
    void afterDeploymentValidation(@Observes final AfterDeploymentValidation event,
            final BeanManager beanManager) {

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "MVPExtension.afterDeploymentValidation");
        }
        // Check that all MVPPresenter Beans are annotated with ViewInterface
        final Iterator<Bean<?>> beanIterator = beanManager.getBeans(
                MVPPresenter.class).iterator();
        while (beanIterator.hasNext()) {
            final Bean<?> bean = beanIterator.next();
            if (bean.getBeanClass().getAnnotation(
                    AbstractMVPPresenter.ViewInterface.class) == null
                    && bean.getBeanClass().getAnnotation(
                            AbstractUIScopedMVPPresenter.ViewInterface.class) == null) {
                event.addDeploymentProblem(new IllegalArgumentException(
                        bean.getBeanClass().getName()
                        + " must be annoteded with @ViewInterface annotation"));
            }
            // Should we also check that we added the Observer method ?
        }
    }

    public <X> void onProcessManagedBean(@Observes final ProcessManagedBean<X> event,
            final BeanManager beanManager) {

        if (MVPPresenter.class.isAssignableFrom(event.getBean().getBeanClass())) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST,
                        "MVPExtension.onProcessManagedBean  found presenter bean {0}",
                        event.getBean().getBeanClass());
            }
            presenterBeans.add(event.getBean());
        }
    }

    /*
     * We probably only need onProcessManagedBean
     */
    public <X> void onProcessBean(@Observes final ProcessBean<X> event,
            final BeanManager beanManager) {

        if (MVPPresenter.class.isAssignableFrom(event.getBean().getBeanClass())) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST,
                        "MVPExtension.onProcessBean  found presenter bean {0}",
                        event.getBean().getBeanClass());
            }
            presenterBeans.add(event.getBean());
        }
    }

    /**
     * Adds a View enter observer method for each bean extending
     * {@link AbstractMVPPresenter}.
     *
     * @param afterBeanDiscovery
     * @param beanManager
     */
    void afterBeanDiscovery(
            @Observes final AfterBeanDiscovery afterBeanDiscovery,
            final BeanManager beanManager) {

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST,
                    "MVPExtension.afterBeanDiscovery  {0}", presenterBeans);
        }
        final Iterator<Bean<?>> beanIterator = presenterBeans.iterator();
        while (beanIterator.hasNext()) {
            final Bean<?> bean = beanIterator.next();
            afterBeanDiscovery
                    .addObserverMethod(new ObserverMethod<ParameterDTO>() {
                        @Override
                        public Class<?> getBeanClass() {
                            return bean.getBeanClass();
                        }

                        @Override
                        public Set<Annotation> getObservedQualifiers() {
                            final Set<Annotation> qualifiers = new HashSet<Annotation>();
                            if (getBeanClass().getAnnotation(
                                    AbstractMVPPresenter.ViewInterface.class) != null) {
                            final Class<? extends MVPView> viewInterface = getBeanClass()
                                .getAnnotation(AbstractMVPPresenter.ViewInterface.class).value();
                            qualifiers.add(new CDIEventImpl(viewInterface
                                            .getName() + AbstractMVPPresenter.VIEW_ENTER));
                            return qualifiers;
                            } else if (getBeanClass().getAnnotation(
                                    AbstractUIScopedMVPPresenter.ViewInterface.class) != null) {
                                final Class<? extends MVPView> viewInterface = getBeanClass()
                                .getAnnotation(AbstractUIScopedMVPPresenter.ViewInterface.class).value();
                                qualifiers.add(new CDIEventImpl(viewInterface
                                                .getName() + AbstractUIScopedMVPPresenter.VIEW_ENTER));
                                return qualifiers;
                            } else {
                                throw new RuntimeException(
                                        "@ViewInterface must be declared for Presenters");
                            }

                        }

                        @Override
                        public Type getObservedType() {
                            return ParameterDTO.class;
                        }

                        @Override
                        public Reception getReception() {
                            return Reception.ALWAYS;
                        }

                        @Override
                        public TransactionPhase getTransactionPhase() {
                            return TransactionPhase.IN_PROGRESS;
                        }

                        @SuppressWarnings("rawtypes")
                        @Override
                        public void notify(final ParameterDTO event) {
                            final Object presenter = beanManager.getReference(
                                    bean, getBeanClass(),
                                    beanManager.createCreationalContext(bean));
                            if (presenter instanceof AbstractMVPPresenter) {
                            ((AbstractMVPPresenter) presenter).viewEntered();
                            } else if (presenter instanceof AbstractUIScopedMVPPresenter) {
                                ((AbstractUIScopedMVPPresenter) presenter).viewEntered();
                            }
                        }
                    });
        }
    }
}
