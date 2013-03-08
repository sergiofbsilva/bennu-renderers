package pt.ist.bennu.renderers.extensions.validators;

import org.apache.commons.lang.StringUtils;

import pt.ist.bennu.renderers.core.components.HtmlFormComponent;
import pt.ist.bennu.renderers.core.components.HtmlSimpleValueComponent;
import pt.ist.bennu.renderers.core.utils.RenderUtils;
import pt.ist.bennu.renderers.core.validators.HtmlChainValidator;
import pt.ist.bennu.renderers.core.validators.HtmlValidator;
import pt.ist.bennu.renderers.extensions.AutoCompleteInputRenderer;

public class RequiredAutoCompleteSelectionValidator extends HtmlValidator {

    private boolean allowsCustom = false;

    public RequiredAutoCompleteSelectionValidator() {
        super();
    }

    public RequiredAutoCompleteSelectionValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
    }

    @Override
    public void performValidation() {
        HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) getComponent();

        String value = component.getValue();
        if (value == null || value.length() == 0
                || (!this.isAllowsCustom() && value.equals(AutoCompleteInputRenderer.TYPING_VALUE))) {
            setValid(false);
        } else {
            setValid(true);
        }
    }

    @Override
    public boolean hasJavascriptSupport() {
        return true;
    }

    @Override
    protected String getSpecificValidatorScript() {

        StringBuilder validatorBuilder =
                new StringBuilder(
                        "function(element) { return $(element).prevAll(\"input[name$=_AutoComplete]\").attr('value').length > 0 ");
        if (this.isAllowsCustom()) {
            validatorBuilder.append("|| ($(element).prevAll(\"input[name$=_AutoComplete]\").attr('value') == 'custom')");
        } else {
            validatorBuilder.append("&& ($(element).prevAll(\"input[name$=_AutoComplete]\").attr('value') != 'custom')");
        }
        validatorBuilder.append("; }");

        return validatorBuilder.toString();
    }

    @Override
    protected String bindJavascriptEventsTo(HtmlFormComponent formComponent) {
        return "#" + RenderUtils.escapeId(formComponent.getId().replace("_AutoComplete", ""));
    }

    @Override
    protected String getValidatableId(HtmlFormComponent formComponent) {
        return RenderUtils.escapeId(formComponent.getId().replace("_AutoComplete", ""));
    }

    @Override
    public String getMessage() {
        if (StringUtils.isEmpty(super.getMessage())) {
            setMessage("renderers.validator.autoComplete.required");
        }
        return super.getMessage();
    }

    public boolean isAllowsCustom() {
        return allowsCustom;
    }

    public void setAllowsCustom(boolean allowsCustom) {
        this.allowsCustom = allowsCustom;
    }

}