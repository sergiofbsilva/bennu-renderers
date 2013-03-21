package pt.ist.bennu.renderers.rendererExtensions.validators;

import pt.ist.bennu.renderers.core.validators.EmailValidator;
import pt.ist.bennu.renderers.core.validators.HtmlChainValidator;

public class NotRequiredEmailValidator extends EmailValidator {

    public NotRequiredEmailValidator() {
        super();
    }

    public NotRequiredEmailValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
    }

    @Override
    public void performValidation() {
        final String email = getComponent().getValue();
        if (email != null && email.length() > 0) {
            super.performValidation();
        } else {
            setValid(true);
        }
    }

}
