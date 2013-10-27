package org.vaadin.addon.cdimvp.test.stubs;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class TestUI extends UI implements Bean {
    @Override
    protected void init(VaadinRequest request) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object create(CreationalContext creationalContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void destroy(Object instance, CreationalContext creationalContext) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set getTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set getQualifiers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class getScope() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set getStereotypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class getBeanClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAlternative() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isNullable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Set getInjectionPoints() {
        // TODO Auto-generated method stub
        return null;
    }

}
