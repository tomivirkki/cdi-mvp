package org.vaadin.addon.cdimvp;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ObserverMethod;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;
import org.vaadin.addon.cdimvp.CDIEvent.CDIEventImpl;

@SuppressWarnings("serial")
public class MVPExtension implements Extension, Serializable {
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

        final Iterator<Bean<?>> beanIterator = beanManager.getBeans(
                MVPPresenter.class).iterator();
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
                                    ViewInterface.class) == null) {
                                throw new RuntimeException(
                                        "@ViewInterface must be declared for Presenters");
                            }
                            final Class<? extends MVPView> viewInterface = getBeanClass()
                                    .getAnnotation(ViewInterface.class).value();
                            qualifiers.add(new CDIEventImpl(viewInterface
                                    .getName() + AbstractMVPPresenter.VIEW_ENTER));
                            return qualifiers;
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
                            ((AbstractMVPPresenter) presenter).viewEntered();
                        }
                    });
        }
    }
}
