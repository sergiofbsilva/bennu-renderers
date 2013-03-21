package pt.ist.fenixWebFramework.renderers;

import org.apache.commons.beanutils.PropertyUtils;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlLink.Target;
import pt.ist.fenixWebFramework.renderers.components.HtmlLinkWithPreprendedComment;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.state.ViewDestination;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter;

/**
 * This render is used to create a link out of an object. You choose the link
 * format and some properties can be used to configure the link. You can also
 * specify the link indirectly by specifing a destination and then defining a
 * destination with that name in the place were ou use this renderer.
 * 
 * <p>
 * The link body is configured through a sub rendering of the object with the specified layout and schema.
 * 
 * <p>
 * Example: <a href="#">Jane Doe</a>
 * 
 * @author cfgi
 */
public class ObjectLinkRenderer extends OutputRenderer {

    private boolean useParent;

    private String linkFormat;

    private boolean contextRelative;
    private boolean moduleRelative;

    private String destination;

    private String subSchema;

    private String subLayout;

    private String key;

    private String bundle;

    private String text;

    private String linkIf;

    private boolean blankTarget = false;

    private boolean indentation = false;

    private boolean hasContext = false;

    private boolean hasChecksum = true;

    private String format;

    public String getFormat() {
        return this.format;
    }

    /**
     * This allows to specify a presentation format for each object. For more
     * details about the format syntaxt check the {@see FormatRenderer}.
     * 
     * @property
     */
    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isBlankTarget() {
        return blankTarget;
    }

    /**
     * This property allows you to specify if the link opens in a new window or
     * not. Defaults to false.
     * 
     * @property
     */
    public void setBlankTarget(boolean blankTarget) {
        this.blankTarget = blankTarget;
    }

    public String getLinkFormat() {
        return this.linkFormat;
    }

    /**
     * This property allows you to specify the format of the final link. In this
     * format you can use properties of the object being presented. For example:
     * 
     * <code>
     *  format="/some/action.do?oid=${id}"
     * </code>
     * 
     * @see RenderUtils#getFormattedProperties(String, Object)
     * @property
     */
    public void setLinkFormat(String linkFormat) {
        this.linkFormat = linkFormat;
    }

    public boolean isContextRelative() {
        return this.contextRelative;
    }

    /**
     * Indicates that the link specified should be relative to the context of
     * the application and not to the current module. This also overrides the
     * module if a destination is specified.
     * 
     * @property
     */
    public void setContextRelative(boolean contextRelative) {
        this.contextRelative = contextRelative;
    }

    public boolean isModuleRelative() {
        return this.moduleRelative;
    }

    /**
     * Allows you to choose if the generated link is relative to the current
     * module. Note that if the link is not context relative then it also isn't
     * module relative.
     * 
     * @property
     */
    public void setModuleRelative(boolean moduleRelative) {
        this.moduleRelative = moduleRelative;
    }

    public boolean isUseParent() {
        return this.useParent;
    }

    /**
     * This property can be used when presenting an object's slot. If this
     * property is true the object that will be considered when replacing the
     * properties in the link will be the parent object, that is, the object
     * that contains the slot being presented.
     * 
     * <p>
     * Off course, if this property is false (the default) the object that will be considered is the object initialy being
     * presented.
     * 
     * @property
     */
    public void setUseParent(boolean useParent) {
        this.useParent = useParent;
    }

    public String getDestination() {
        return this.destination;
    }

    /**
     * This property is an alternative to the use of the {@link #setLinkFormat(String) linkFormat}. With this property you can
     * specify the name of the view state destination that will be used. This
     * property allows you to select the concrete destination in each context
     * were this configuration is used.
     * 
     * @property
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getText() {
        return this.text;
    }

    /**
     * The text to appear as the link text. This is a simple alternative to the
     * full presentation of the object.
     * 
     * @property
     */
    public void setText(String text) {
        this.text = text;
    }

    public String getKey() {
        return this.key;
    }

    /**
     * Instead of specifying thr {@link #setText(String) text} property you can
     * specify a key, with this property, and a bundle with the {@link #setBundle(String) bundle}.
     * 
     * @property
     */
    public void setKey(String key) {
        this.key = key;
    }

    public String getBundle() {
        return this.bundle;
    }

    /**
     * The bundle were the {@link #setKey(String) key} will be fetched.
     * 
     * @property
     */
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getSubLayout() {
        return this.subLayout;
    }

    /**
     * Specifies the sub layout that will be used for the body of the link, that
     * is, the object will be presented using the layout specified and the
     * result of that presentation will be the body of the link.
     * 
     * @property
     */
    public void setSubLayout(String subLayout) {
        this.subLayout = subLayout;
    }

    public String getSubSchema() {
        return this.subSchema;
    }

    /**
     * The name of the schema to use in the presentation of the object for the
     * body of the link.
     * 
     * @property
     */
    public void setSubSchema(String subSchema) {
        this.subSchema = subSchema;
    }

    public String getLinkIf() {
        return this.linkIf;
    }

    /**
     * Name of the property to use when determining if we should really do a
     * link or not.
     * 
     * @property
     */
    public void setLinkIf(String linkIf) {
        this.linkIf = linkIf;
    }

    /**
     * Chooses if the generated elements should be indented or not. This can be
     * usefull when you want to introduce a separator but need to remove extra
     * spaces.
     * 
     * @property
     */
    public void setIndentation(boolean indentation) {
        this.indentation = indentation;
    }

    public boolean isIndentation() {
        return this.indentation;
    }

    public boolean getHasContext() {
        return hasContext;
    }

    public void setHasContext(boolean hasContext) {
        this.hasContext = hasContext;
    }

    public boolean getHasChecksum() {
        return hasChecksum;
    }

    public void setHasChecksum(boolean hasChecksum) {
        this.hasChecksum = hasChecksum;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                Object usedObject = getTargetObject(object);

                if (usedObject == null) {
                    return new HtmlText();
                }

                if (isAllowedToLink(usedObject)) {
                    HtmlLink link = getLink(usedObject);
                    link.setIndented(isIndentation());

                    String text = getLinkText();
                    if (text != null) {
                        link.setText(text);
                    } else if (getFormat() != null) {
                        link.setText(RenderUtils.getFormattedProperties(getFormat(), usedObject));
                    } else {
                        link.setBody(getLinkBody(object));
                    }

                    if (isBlankTarget()) {
                        link.setTarget(Target.BLANK);
                    }

                    return link;
                } else {
                    return getLinkBody(object);
                }
            }

            private boolean isAllowedToLink(Object usedObject) {
                if (getLinkIf() == null) {
                    return true;
                } else {
                    try {
                        Object object = PropertyUtils.getProperty(usedObject, getLinkIf());
                        if (object == null) {
                            return true;
                        } else {
                            return (Boolean) object;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }

            public HtmlComponent getLinkBody(Object object) {
                Schema findSchema = RenderKit.getInstance().findSchema(getSubSchema());
                return renderValue(object, findSchema, getSubLayout());
            }

            private String getLinkText() {
                if (getText() != null) {
                    return getText();
                }

                if (getKey() == null) {
                    return null;
                }

                return RenderUtils.getResourceString(getBundle(), getKey());
            }

            private HtmlLink getLink(Object usedObject) {
                HtmlLink link = new HtmlLink();

                if (getHasContext()) {
                    link =
                            new HtmlLinkWithPreprendedComment(
                                    !getHasChecksum() ? GenericChecksumRewriter.NO_CHECKSUM_PREFIX_HAS_CONTEXT_PREFIX : RequestRewriter.HAS_CONTEXT_PREFIX);
                } else {
                    link =
                            !getHasChecksum() ? new HtmlLinkWithPreprendedComment(GenericChecksumRewriter.NO_CHECKSUM_PREFIX) : new HtmlLink();
                }

                String url;

                if (getDestination() != null) {
                    ViewDestination destination = getContext().getViewState().getDestination(getDestination());

                    if (destination != null) {
                        link.setModule(destination.getModule());
                        url = destination.getPath();
                    } else {
                        url = "#";
                    }
                } else {
                    if (getLinkFormat() != null) {
                        url = getLinkFormat();
                    } else {
                        url = "#";
                    }
                }

                link.setUrl(RenderUtils.getFormattedProperties(url, usedObject));

                link.setModuleRelative(isModuleRelative());
                link.setContextRelative(isContextRelative());

                return link;
            }

        };
    }

    protected Object getTargetObject(Object object) {
        if (isUseParent()) {
            if (getContext().getParentContext() != null) {
                return getContext().getParentContext().getMetaObject().getObject();
            } else {
                return null;
            }
        } else {
            return object;
        }
    }

}
