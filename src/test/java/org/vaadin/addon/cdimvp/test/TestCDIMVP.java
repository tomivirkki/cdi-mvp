package org.vaadin.addon.cdimvp.test;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vaadin.addon.cdimvp.test.stubs.TestUI;
import org.vaadin.addon.cdimvp.test.stubs.UIScopedTestPresenter;
import org.vaadin.addon.cdimvp.test.stubs.UIScopedTestView;
import org.vaadin.addon.cdimvp.test.stubs.UIScopedTestViewImpl;
import org.vaadin.addon.cdimvp.test.stubs.ViewScopedTestPresenter;
import org.vaadin.addon.cdimvp.test.stubs.ViewScopedTestViewImpl;

import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.internal.UIBean;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.util.CurrentInstance;

@RunWith(Arquillian.class)
public class TestCDIMVP {
    @Deployment
    public static JavaArchive createDeployment() {
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies().resolve()
                .withTransitivity().asFile();

        JavaArchive jar = ShrinkWrap
                .create(JavaArchive.class)
                .addPackage("org.vaadin.addon.cdimvp")
                .addClass(UIScopedTestView.class)
                .addClass(UIScopedTestViewImpl.class)
                .addClass(ViewScopedTestViewImpl.class)
                .addClass(ViewScopedTestPresenter.class)
                .addClass(UIScopedTestPresenter.class)
                .addClass(TestUI.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource(
                        "META-INF/services/javax.enterprise.inject.spi.Extension");

        for (File lib : libs) {
            if (lib.getAbsolutePath().contains("deltaspike")
                    || lib.getAbsolutePath().contains("vaadin-cdi")) {
                JavaArchive jarArchive = ShrinkWrap.createFromZipFile(
                        JavaArchive.class, lib);
                jar.merge(jarArchive);
            }
        }

        return jar;
    }

    @BeforeClass
    public static void init() {
        // Prepare a simulated environment for CDI testing
        TestUI testUI = new TestUI();
        VerticalLayout navContainer = new VerticalLayout();
        CurrentInstance.set(VerticalLayout.class, navContainer);

        Navigator nav = new Navigator(testUI, navContainer);
        testUI.setNavigator(nav);
        CurrentInstance.set(UI.class, testUI);

        Page page = testUI.getPage();

        try {
            for (Field field : Page.class.getDeclaredFields()) {
                if ("location".equals(field.getName())) {
                    field.setAccessible(true);
                    field.set(page, new URI("http://localhost:8080/test"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        VaadinSession session = new VaadinSession(null);
        session.setAttribute("cdi-session-id", 2l);
        CurrentInstance.set(VaadinSession.class, session);

        UIBean uiBean = new UIBean(testUI, 1);
        CurrentInstance.set(UIBean.class, uiBean);
    }

    @Inject
    private Instance<UIScopedTestPresenter> presenter;
    @Inject
    private Instance<UIScopedTestViewImpl> view;
    @Inject
    private Instance<CDIViewProvider> cdiViewProvider;

    @Test
    public void viewEqualsTest() {
        Assert.assertEquals(view.get(), presenter.get().getView());
    }

    @Test
    public void viewEnteredTest() {
        Assert.assertFalse(presenter.get().isViewEntered());
        Assert.assertFalse(view.get().isEntered());
        view.get().enter();
        Assert.assertTrue(presenter.get().isViewEntered());
        Assert.assertTrue(view.get().isEntered());
    }

    @Test
    public void eventTest() {
        Navigator navi = UI.getCurrent().getNavigator();
        navi.addProvider(cdiViewProvider.get());
        navi.navigateTo("viewscopedtestview");

        Assert.assertFalse(view.get().isEventHandled());
        Assert.assertNull(presenter.get().getEventParameters());
        view.get().fireAnEvent();
        Assert.assertTrue(view.get().isEventHandled());
        Assert.assertEquals(1, presenter.get().getEventParameters()
                .getPrimaryParameter());
        Assert.assertEquals(new Integer(2), presenter.get()
                .getEventParameters().getSecondaryParameter(0, Integer.class));
        Assert.assertEquals(new Integer(3), presenter.get()
                .getEventParameters().getSecondaryParameter(1, Integer.class));
    }

    @Test
    public void navigationTest() {
        Navigator navi = UI.getCurrent().getNavigator();
        navi.addProvider(cdiViewProvider.get());
        // Navigate to a View scoped view
        navi.navigateTo("viewscopedtestview");
        ViewScopedTestViewImpl viewScoped = currentView();
        Assert.assertFalse(viewScoped.isEventHandled());
        viewScoped.fireAnEvent();
        Assert.assertTrue(viewScoped.isEventHandled());

        // Navigate away to a UI scoped view
        navi.navigateTo("uiscopedtestview");
        UIScopedTestViewImpl uiScoped = currentView();
        Assert.assertEquals(uiScoped, view.get());
        uiScoped.fireAnEvent();

        // Navigate back to a View scoped view (should be a new instance)
        navi.navigateTo("viewscopedtestview");
        viewScoped = currentView();
        Assert.assertFalse(viewScoped.isEventHandled());

        // Navigate back to a UI scoped view (should be the same instance)
        navi.navigateTo("uiscopedtestview");
        uiScoped = currentView();
        Assert.assertTrue(uiScoped.isEventHandled());
    }

    private <T> T currentView() {
        VerticalLayout container = CurrentInstance.get(VerticalLayout.class);
        return (T) container.getComponent(0);
    }
}
