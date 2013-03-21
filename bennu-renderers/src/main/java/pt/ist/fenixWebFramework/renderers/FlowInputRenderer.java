package pt.ist.fenixWebFramework.renderers;

import java.util.Iterator;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlLabel;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.FlowLayout;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;

/**
 * This input renderer allows several slots to be edited without any special
 * formating. Each slot's editor is placed after the previous. You should note
 * that this technique does not work well for more than two or three slots. You
 * can decreese the space taken by suppresing the slots labels but then you need
 * other way of identifing wich slot is beeing edited.
 * 
 * <p>
 * Example: Name: <input type="text"/> Age: <input type="text"/>
 * 
 * @author cfgi
 */
public class FlowInputRenderer extends InputRenderer {

    private String eachClasses;
    private String eachStyle;
    private String eachSchema;
    private String eachLayout;

    private boolean eachInline = true;
    private boolean labelExcluded = false;

    private String labelTerminator;
    private String labelClasses;
    private String labelStyle;

    private boolean hideValidators;
    private String validatorClasses;

    public String getEachClasses() {
        return this.eachClasses;
    }

    /**
     * Allows to specify the class attribute for each one of the slot's
     * presentation.
     * 
     * @property
     */
    public void setEachClasses(String eachClasses) {
        this.eachClasses = eachClasses;
    }

    public boolean isEachInline() {
        return this.eachInline;
    }

    /**
     * This property selects if each slot should be presented as an inline
     * element or as a block element. By default slots are presented inline,
     * that is, they will be added inside <tt>span</tt> elements. If <tt>eachInline</tt> is <tt>false</tt> then a <tt>div</tt>
     * will be
     * used.
     * 
     * @property
     */
    public void setEachInline(boolean eachInline) {
        this.eachInline = eachInline;
    }

    public String getEachLayout() {
        return this.eachLayout;
    }

    /**
     * The layout to be used in each slot's presentation.
     * 
     * @property
     */
    public void setEachLayout(String eachLayout) {
        this.eachLayout = eachLayout;
    }

    public String getEachSchema() {
        return this.eachSchema;
    }

    /**
     * The schema to be used in each of the slot's presentation.
     * 
     * @property
     */
    public void setEachSchema(String eachSchema) {
        this.eachSchema = eachSchema;
    }

    public String getEachStyle() {
        return this.eachStyle;
    }

    /**
     * The value of the style attribute of each slot's presentation.
     * 
     * @property
     */
    public void setEachStyle(String eachStyle) {
        this.eachStyle = eachStyle;
    }

    public boolean isLabelExcluded() {
        return this.labelExcluded;
    }

    /**
     * Whether the label should be presented before each editor or not.
     * 
     * @property
     */
    public void setLabelExcluded(boolean labelExcluded) {
        this.labelExcluded = labelExcluded;
    }

    public boolean isHideValidators() {
        return this.hideValidators;
    }

    /**
     * Allows you to suppress the inclusion of the validator messages in the
     * standard layout. This is specilly usefull if you want to show all
     * messages in one place in the page.
     * 
     * @property
     */
    public void setHideValidators(boolean hideValidators) {
        this.hideValidators = hideValidators;
    }

    public String getValidatorClasses() {
        return this.validatorClasses;
    }

    /**
     * Configure the html classes to apply to the validator messages.
     * 
     * @property
     */
    public void setValidatorClasses(String validatorClasses) {
        this.validatorClasses = validatorClasses;
    }

    public String getLabelTerminator() {
        return this.labelTerminator;
    }

    /**
     * Chooses the suffix to be added to each label. If the label already
     * contains that suffix then nothing will be added. See {@link StandardObjectRenderer#setLabelTerminator(String)}.
     * 
     * @property
     */
    public void setLabelTerminator(String labelTerminator) {
        this.labelTerminator = labelTerminator;
    }

    public String getLabelClasses() {
        return labelClasses;
    }

    /**
     * Allows to specify the class of the element around the generated label
     * (when shown)
     * 
     * @property
     */
    public void setLabelClasses(String labelClasses) {
        this.labelClasses = labelClasses;
    }

    public String getLabelStyle() {
        return labelStyle;
    }

    /**
     * Allows to specify the style applied to the generated label (when shown)
     * 
     * @property
     */
    public void setLabelStyle(String labelStyle) {
        this.labelStyle = labelStyle;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new FlowObjectInputRenderer(getInputContext().getMetaObject());
    }

    private class FlowObjectInputRenderer extends FlowLayout {

        private Iterator<MetaSlot> iterator;

        public FlowObjectInputRenderer(MetaObject object) {
            this.iterator = object.getSlots().iterator();
        }

        @Override
        protected boolean hasMoreComponents() {
            return iterator.hasNext();
        }

        @Override
        protected HtmlComponent getNextComponent() {
            MetaSlot slot = iterator.next();

            HtmlComponent component = renderSlot(slot);
            HtmlFormComponent validatedComponent = (HtmlFormComponent) findValidatableComponent(component);
            HtmlChainValidator chainValidator = getChainValidator(validatedComponent, slot);

            if (isLabelExcluded() && (chainValidator == null || chainValidator.isEmpty())) {
                return component;
            }

            return createContainer(slot, component, validatedComponent, chainValidator);
        }

        private HtmlInlineContainer createContainer(MetaSlot slot, HtmlComponent component, HtmlFormComponent validated,
                HtmlChainValidator chainValidator) {
            HtmlInlineContainer container = new HtmlInlineContainer();

            if (!isLabelExcluded()) {

                if (slot.isReadOnly()) {
                    container.addChild(new HtmlText(addLabelTerminator(slot.getLabel()), false));
                } else {
                    HtmlLabel label = new HtmlLabel();
                    label.setFor(slot.getKey().toString());
                    label.setText(addLabelTerminator(slot.getLabel()));
                    label.setClasses(getLabelClasses());
                    label.setStyle(getLabelStyle());

                    container.addChild(label);
                }
            }

            container.addChild(component);

            if (chainValidator != null && !isHideValidators()) {
                chainValidator.setClasses(getValidatorClasses());
                container.addChild(chainValidator);
            }

            return container;
        }

        // duplicated code id=standard-renderer.label.addTerminator
        protected String addLabelTerminator(String label) {
            if (getLabelTerminator() == null) {
                return label;
            }

            if (label == null) {
                return null;
            }

            if (label.endsWith(getLabelTerminator())) {
                return label;
            }

            return label + getLabelTerminator();
        }
    }
}
