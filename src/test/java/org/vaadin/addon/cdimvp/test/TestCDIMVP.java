package org.vaadin.addon.cdimvp.test;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vaadin.addon.cdimvp.test.stubs.TestPresenter;
import org.vaadin.addon.cdimvp.test.stubs.TestUI;
import org.vaadin.addon.cdimvp.test.stubs.TestView;
import org.vaadin.addon.cdimvp.test.stubs.TestViewImpl;

import com.vaadin.cdi.internal.BeanStoreContainer;
import com.vaadin.cdi.internal.UIBean;
import com.vaadin.util.CurrentInstance;

@RunWith(Arquillian.class)
public class TestCDIMVP {
    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive jar = ShrinkWrap
                .create(JavaArchive.class)
                .addPackage("org.vaadin.addon.cdimvp")
                .addClass(TestView.class)
                .addClass(TestViewImpl.class)
                .addClass(TestPresenter.class)
                .addClass(TestUI.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource(
                        "META-INF/services/javax.enterprise.inject.spi.Extension")
                .addClass(BeanStoreContainer.class);
        return jar;
    }

    @BeforeClass
    public static void init() {
        CurrentInstance.set(UIBean.class, new UIBean(new TestUI(), 1));
    }

    @Inject
    private TestPresenter presenter;
    @Inject
    private TestViewImpl view;

    @Test
    public void viewEqualsTest() {
        Assert.assertEquals(view, presenter.getView());
    }

    @Test
    public void viewEnteredTest() {
        Assert.assertFalse(presenter.isViewEntered());
        Assert.assertFalse(view.isEntered());
        view.enter();
        Assert.assertTrue(presenter.isViewEntered());
        Assert.assertTrue(view.isEntered());
    }

    @Test
    public void eventTest() {
        Assert.assertFalse(view.isEventHandled());
        Assert.assertNull(presenter.getEventParameters());
        view.fireAnEvent();
        Assert.assertTrue(view.isEventHandled());
        Assert.assertEquals(1, presenter.getEventParameters()
                .getPrimaryParameter());
        Assert.assertEquals(new Integer(2), presenter.getEventParameters()
                .getSecondaryParameter(0, Integer.class));
        Assert.assertEquals(new Integer(3), presenter.getEventParameters()
                .getSecondaryParameter(1, Integer.class));
    }

}
