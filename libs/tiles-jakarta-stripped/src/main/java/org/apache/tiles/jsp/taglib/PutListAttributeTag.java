/*
 * This file was automatically generated by Apache Tiles Autotag.
 */
package org.apache.tiles.jsp.taglib;

import java.io.IOException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.autotag.core.runtime.AutotagRuntime;

/**
 * <p>
 * <strong>Declare a list that will be pass as attribute to tile. </strong>
 * </p>
 * <p>
 * Declare a list that will be pass as attribute to tile. List elements are
 * added using the tags 'addAttribute' or 'addListAttribute'. This tag can only
 * be used inside 'insertTemplate', 'insertDefinition', 'definition' tags.
 * </p>
 */
public class PutListAttributeTag extends SimpleTagSupport {

    /**
     * The template model.
     */
    private final org.apache.tiles.template.PutListAttributeModel model
            = new org.apache.tiles.template.PutListAttributeModel();

    /**
     * The name of the attribute to put.
     */
    private java.lang.String name;

    /**
     * A comma-separated list of roles. If present, the attribute
     * will be rendered only if the current user belongs to one of the roles.
     */
    private java.lang.String role;

    /**
     * If <code>true</code>, the list attribute will use, as first elements, the
     * list contained in the list attribute, put with the same name, of the containing definition.
     */
    private boolean inherit;

    /**
     * If <code>true</code> the attribute will be cascaded to all nested attributes.
     */
    private boolean cascade;

    /**
     * Getter for name property.
     *
     * @return
     * The name of the attribute to put.
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * Setter for name property.
     *
     * @param name
     * The name of the attribute to put.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * Getter for role property.
     *
     * @return
     * A comma-separated list of roles. If present, the attribute
     * will be rendered only if the current user belongs to one of the roles.
     */
    public java.lang.String getRole() {
        return role;
    }

    /**
     * Setter for role property.
     *
     * @param role
     * A comma-separated list of roles. If present, the attribute
     * will be rendered only if the current user belongs to one of the roles.
     */
    public void setRole(java.lang.String role) {
        this.role = role;
    }

    /**
     * Getter for inherit property.
     *
     * @return
     * If <code>true</code>, the list attribute will use, as first elements, the
     * list contained in the list attribute, put with the same name, of the containing definition.
     */
    public boolean isInherit() {
        return inherit;
    }

    /**
     * Setter for inherit property.
     *
     * @param inherit
     * If <code>true</code>, the list attribute will use, as first elements, the
     * list contained in the list attribute, put with the same name, of the containing definition.
     */
    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    /**
     * Getter for cascade property.
     *
     * @return
     * If <code>true</code> the attribute will be cascaded to all nested attributes.
     */
    public boolean isCascade() {
        return cascade;
    }

    /**
     * Setter for cascade property.
     *
     * @param cascade
     * If <code>true</code> the attribute will be cascaded to all nested attributes.
     */
    public void setCascade(boolean cascade) {
        this.cascade = cascade;
    }

    /** {@inheritDoc} */
    @Override
    public void doTag() throws JspException, IOException {
        AutotagRuntime<org.apache.tiles.request.Request> runtime
                = new org.apache.tiles.request.jsp.autotag.JspAutotagRuntime();
        SimpleTagSupport tag = (SimpleTagSupport) runtime;
        tag.setJspContext(getJspContext());
        tag.setJspBody(getJspBody());
        tag.setParent(getParent());
        tag.doTag();
        org.apache.tiles.request.Request request = runtime.createRequest();
        ModelBody modelBody = runtime.createModelBody();
        model.execute(
            name,
            role,
            inherit,
            cascade,
            request, modelBody
        );
    }
}
