package pt.ist.fenixWebFramework.renderers.validators;

public class ISSNValidator extends RegexpValidator {

    private boolean required;

    public ISSNValidator() {
        super("[0-9]{4}-[0-9]{3}[0-9X]");
        setMessage("renderers.validator.issn");
        setKey(true);
        setRequired(false);
    }

    public ISSNValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator, "[0-9]{4}-[0-9]{3}[0-9X]");
        setMessage("renderers.validator.issn");
        setKey(true);
        setRequired(false);
    }

    @Override
    public void performValidation() {
        if (hasValue()) {
            super.performValidation();
            if (isValid()) {
                setValid(isCheckSumValid(getComponent().getValue()));
            }
        } else {
            setValid(!isRequired());
        }
    }

    private boolean isCheckSumValid(String value) {
        value = value.replaceAll("-", "");
        int res = 0;
        for (int i = 0; i < 7; i++) {
            res += (8 - i) * (value.charAt(i) - '0');
        }

        int remainder = res % 11;
        int checkDigit = 11 - remainder;

        char result;
        if (remainder == 0) {
            result = '0';
        } else {
            if (checkDigit == 10) {
                result = 'X';
            } else {
                result = (char) (checkDigit + '0');
            }
        }
        return value.charAt(7) == result;
    }

    private boolean hasValue() {
        return (getComponent().getValue() != null && getComponent().getValue().length() > 0);
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

}
