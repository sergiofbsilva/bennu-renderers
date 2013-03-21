package pt.ist.fenixWebFramework.rendererExtensions.validators;

import pt.ist.fenixWebFramework.renderers.validators.EmailValidator;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;

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
