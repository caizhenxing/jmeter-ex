/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jmeter.testbeans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.jmeter.testbeans.gui.GenericTestBeanCustomizer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * Support class for test bean beanInfo objects. It will help using the
 * introspector to get most of the information, to then modify it at will.
 * <p>
 * To use, subclass it, create a subclass with a parameter-less constructor
 * that:
 * <ol>
 * <li>Calls super(beanClass)
 * <li>Modifies the property descriptors, bean descriptor, etc. at will.
 * </ol>
 * <p>
 * Even before any such modifications, a resource bundle named xxxResources
 * (where xxx is the fully qualified bean class name) will be obtained if
 * available and used to localize the following:
 * <ul>
 * <li>Bean's display name -- from property <b>displayName</b>.
 * <li>Properties' display names -- from properties <b><i>propertyName</i>.displayName</b>.
 * <li>Properties' short descriptions -- from properties <b><i>propertyName</i>.shortDescription</b>.
 * </ul>
 * <p>
 * The resource bundle will be stored as the bean descriptor's "resourceBundle"
 * attribute, so that it can be used for further localization. TestBeanGUI, for
 * example, uses it to obtain the group's display names from properties <b><i>groupName</i>.displayName</b>.
 *
 * @version $Revision: 674365 $
 */
public abstract class BeanInfoSupport extends SimpleBeanInfo {

    private static final Logger log = LoggingManager.getLoggerForClass();

    // Some known attribute names, just for convenience:
    public static final String TAGS = GenericTestBeanCustomizer.TAGS;

    public static final String NOT_UNDEFINED = GenericTestBeanCustomizer.NOT_UNDEFINED;

    public static final String NOT_EXPRESSION = GenericTestBeanCustomizer.NOT_EXPRESSION;

    public static final String NOT_OTHER = GenericTestBeanCustomizer.NOT_OTHER;

    public static final String DEFAULT = GenericTestBeanCustomizer.DEFAULT;

    public static final String RESOURCE_BUNDLE = GenericTestBeanCustomizer.RESOURCE_BUNDLE;

    /** The class for which we're providing the bean info. */
    // NOTREAD private Class beanClass;
    /** The BeanInfo for our class as obtained by the introspector. */
    private BeanInfo rootBeanInfo;

    /** The icons for this bean. */
    private Image[] icons = new Image[5];

    /**
     * Construct a BeanInfo for the given class.
     */
    protected BeanInfoSupport(Class beanClass) {
        // NOTREAD this.beanClass= beanClass;

        try {
            rootBeanInfo = Introspector.getBeanInfo(beanClass, Introspector.IGNORE_IMMEDIATE_BEANINFO);
        } catch (IntrospectionException e) {
            log.error("Can't introspect.", e);
            throw new Error(e.toString()); // Programming error: bail out.
        }

        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(
                    beanClass.getName() + "Resources",  // $NON-NLS-1$
                    JMeterUtils.getLocale());

            // Store the resource bundle as an attribute of the BeanDescriptor:
            getBeanDescriptor().setValue(RESOURCE_BUNDLE, resourceBundle);
            // Localize the bean name
            try {
                getBeanDescriptor().setDisplayName(resourceBundle.getString("displayName")); // $NON-NLS-1$
            } catch (MissingResourceException e) {
                log.debug("Localized display name not available for bean " + beanClass.getName());
            }
            // Localize the property names and descriptions:
            PropertyDescriptor[] properties = getPropertyDescriptors();
            for (int i = 0; i < properties.length; i++) {
                String name = properties[i].getName();
                try {
                    properties[i].setDisplayName(resourceBundle.getString(name + ".displayName")); // $NON-NLS-1$
                } catch (MissingResourceException e) {
                    log.debug("Localized display name not available for property " + name);
                }

                try {
                    properties[i].setShortDescription(resourceBundle.getString(name + ".shortDescription"));
                } catch (MissingResourceException e) {
                    log.debug("Localized short description not available for property " + name);
                }
            }
        } catch (MissingResourceException e) {
            log.warn("Localized strings not available for bean " + beanClass, e);
        } catch (Exception e) {
            log.warn("Something bad happened when loading bean info", e);
        }
    }

    /**
     * Get the property descriptor for the property of the given name.
     *
     * @param name
     *            property name
     * @return descriptor for a property of that name, or null if there's none
     */
    protected PropertyDescriptor property(String name) {
        PropertyDescriptor[] properties = getPropertyDescriptors();
        for (int i = 0; i < properties.length; i++) {
            if (properties[i].getName().equals(name)) {
                return properties[i];
            }
        }
        log.warn("Cannot find property: "+name);
        return null;
    }

    /**
     * Set the bean's 16x16 colour icon.
     *
     * @param resourceName
     *            A pathname relative to the directory holding the class file of
     *            the current class.
     */
    protected void setIcon(String resourceName) {
        icons[ICON_COLOR_16x16] = loadImage(resourceName);
    }

    /** Number of groups created so far by createPropertyGroup. */
    private int numCreatedGroups = 0;

    /**
     * Utility method to group and order properties.
     * <p>
     * It will assing the given group name to each of the named properties, and
     * set their order attribute so that they are shown in the given order.
     * <p>
     * The created groups will get order 1, 2, 3,... in the order in which they
     * are created.
     *
     * @param group
     *            name of the group
     * @param names
     *            property names in the desired order
     */
    protected void createPropertyGroup(String group, String[] names) {
        for (int i = 0; i < names.length; i++) {
            log.debug("Getting property for: " + names[i]);
            PropertyDescriptor p = property(names[i]);
            p.setValue(GenericTestBeanCustomizer.GROUP, group);
            p.setValue(GenericTestBeanCustomizer.ORDER, new Integer(i));
        }
        numCreatedGroups++;
        getBeanDescriptor().setValue(GenericTestBeanCustomizer.ORDER(group), new Integer(numCreatedGroups));
    }

    public BeanInfo[] getAdditionalBeanInfo() {
        return rootBeanInfo.getAdditionalBeanInfo();
    }

    public BeanDescriptor getBeanDescriptor() {
        return rootBeanInfo.getBeanDescriptor();
    }

    public int getDefaultEventIndex() {
        return rootBeanInfo.getDefaultEventIndex();
    }

    public int getDefaultPropertyIndex() {
        return rootBeanInfo.getDefaultPropertyIndex();
    }

    public EventSetDescriptor[] getEventSetDescriptors() {
        return rootBeanInfo.getEventSetDescriptors();
    }

    public Image getIcon(int iconKind) {
        return icons[iconKind];
    }

    public MethodDescriptor[] getMethodDescriptors() {
        return rootBeanInfo.getMethodDescriptors();
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return rootBeanInfo.getPropertyDescriptors();
    }
}
