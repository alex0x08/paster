/*
 * This file was automatically generated by Apache Tiles Autotag.
 */
package org.apache.tiles.jsp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.autotag.core.runtime.AutotagRuntime;

/**
 * <p>
 * <strong>Insert a definition.</strong>
 * </p>
 * <p>
 * Insert a definition with the possibility to override and specify parameters
 * (called attributes). A definition can be seen as a (partially or totally)
 * filled template that can override or complete attribute values.
 * <code>&lt;tiles:insertDefinition&gt;</code> allows to define these attributes
 * and pass them to the inserted jsp page, called template. Attributes are
 * defined using nested tag <code>&lt;tiles:putAttribute&gt;</code> or
 * <code>&lt;tiles:putListAttribute&gt;</code>.
 * </p>
 * <p>
 * You must specify <code>name</code> tag attribute, for inserting a definition
 * from definitions factory.
 * </p>
 * <p>
 * <strong>Example : </strong>
 * </p>
 * 
 * <pre>
 * &lt;code&gt;
 *           &lt;tiles:insertDefinition name=&quot;.my.tiles.defininition flush=&quot;true&quot;&gt;
 *              &lt;tiles:putAttribute name=&quot;title&quot; value=&quot;My first page&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;header&quot; value=&quot;/common/header.jsp&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;footer&quot; value=&quot;/common/footer.jsp&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;menu&quot; value=&quot;/basic/menu.jsp&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;body&quot; value=&quot;/basic/helloBody.jsp&quot; /&gt;
 *           &lt;/tiles:insertDefinition&gt;
 *         &lt;/code&gt;
 * </pre>
 */
public class InsertDefinitionTag extends SimpleTagSupport {

    /**
     * The template model.
     */
    private org.apache.tiles.template.InsertDefinitionModel model = new org.apache.tiles.template.InsertDefinitionModel();

    /**
     * The name of the definition to render.
     */
    private java.lang.String definitionName;

    /**
     * If specified, this template will be used instead of the
     * one used by the definition.
     */
    private java.lang.String template;

    /**
     * The type of the template attribute.
     */
    private java.lang.String templateType;

    /**
     * The expression to evaluate to get the value of the template.
     */
    private java.lang.String templateExpression;

    /**
     * A comma-separated list of roles. If present, the definition
     * will be rendered only if the current user belongs to one of the roles.
     */
    private java.lang.String role;

    /**
     * The preparer to use to invoke before the definition is
     * rendered. If specified, it overrides the preparer specified in the
     * definition itself.
     */
    private java.lang.String preparer;

    /**
     * If <code>true</code>, the response will be flushed after the insert.
     */
    private boolean flush;

    /**
     * Getter for name property.
     *
     * @return
     * The name of the definition to render.
     */
    public java.lang.String getName() {
        return definitionName;
    }

    /**
     * Setter for name property.
     *
     * @param definitionName
     * The name of the definition to render.
     */
    public void setName(java.lang.String definitionName) {
        this.definitionName = definitionName;
    }

    /**
     * Getter for template property.
     *
     * @return
     * If specified, this template will be used instead of the
     * one used by the definition.
     */
    public java.lang.String getTemplate() {
        return template;
    }

    /**
     * Setter for template property.
     *
     * @param template
     * If specified, this template will be used instead of the
     * one used by the definition.
     */
    public void setTemplate(java.lang.String template) {
        this.template = template;
    }

    /**
     * Getter for templateType property.
     *
     * @return
     * The type of the template attribute.
     */
    public java.lang.String getTemplateType() {
        return templateType;
    }

    /**
     * Setter for templateType property.
     *
     * @param templateType
     * The type of the template attribute.
     */
    public void setTemplateType(java.lang.String templateType) {
        this.templateType = templateType;
    }

    /**
     * Getter for templateExpression property.
     *
     * @return
     * The expression to evaluate to get the value of the template.
     */
    public java.lang.String getTemplateExpression() {
        return templateExpression;
    }

    /**
     * Setter for templateExpression property.
     *
     * @param templateExpression
     * The expression to evaluate to get the value of the template.
     */
    public void setTemplateExpression(java.lang.String templateExpression) {
        this.templateExpression = templateExpression;
    }

    /**
     * Getter for role property.
     *
     * @return
     * A comma-separated list of roles. If present, the definition
     * will be rendered only if the current user belongs to one of the roles.
     */
    public java.lang.String getRole() {
        return role;
    }

    /**
     * Setter for role property.
     *
     * @param role
     * A comma-separated list of roles. If present, the definition
     * will be rendered only if the current user belongs to one of the roles.
     */
    public void setRole(java.lang.String role) {
        this.role = role;
    }

    /**
     * Getter for preparer property.
     *
     * @return
     * The preparer to use to invoke before the definition is
     * rendered. If specified, it overrides the preparer specified in the
     * definition itself.
     */
    public java.lang.String getPreparer() {
        return preparer;
    }

    /**
     * Setter for preparer property.
     *
     * @param preparer
     * The preparer to use to invoke before the definition is
     * rendered. If specified, it overrides the preparer specified in the
     * definition itself.
     */
    public void setPreparer(java.lang.String preparer) {
        this.preparer = preparer;
    }

    /**
     * Getter for flush property.
     *
     * @return
     * If <code>true</code>, the response will be flushed after the insert.
     */
    public boolean isFlush() {
        return flush;
    }

    /**
     * Setter for flush property.
     *
     * @param flush
     * If <code>true</code>, the response will be flushed after the insert.
     */
    public void setFlush(boolean flush) {
        this.flush = flush;
    }

    /** {@inheritDoc} */
    @Override
    public void doTag() throws JspException, IOException {
        AutotagRuntime<org.apache.tiles.request.Request> runtime = new org.apache.tiles.request.jsp.autotag.JspAutotagRuntime();
        if (runtime instanceof SimpleTagSupport) {
            SimpleTagSupport tag = (SimpleTagSupport) runtime;
            tag.setJspContext(getJspContext());
            tag.setJspBody(getJspBody());
            tag.setParent(getParent());
            tag.doTag();
        }
        org.apache.tiles.request.Request request = runtime.createRequest();        
        ModelBody modelBody = runtime.createModelBody();
        model.execute(
            definitionName,
            template,
            templateType,
            templateExpression,
            role,
            preparer,
            flush,
            request, modelBody
        );
    }
}