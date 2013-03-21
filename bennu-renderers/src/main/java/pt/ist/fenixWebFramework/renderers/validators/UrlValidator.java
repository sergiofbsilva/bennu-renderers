package pt.ist.fenixWebFramework.renderers.validators;

public class UrlValidator extends HtmlValidator {

    private static final String[] validSchemes = new String[] { "http", "https" };

    private static final String DEFAULT_SCHEME = "http";

    private boolean required;

    /**
     * Required constructor.
     */
    public UrlValidator() {
        super();
        setKey(true);
        setMessage("renderers.validator.url");
        setRequired(true);
    }

    public UrlValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
        // default messsage
        setKey(true);
        setMessage("renderers.validator.url");
        setRequired(true);
    }

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public void performValidation() {
        if (hasValue()) {
            org.apache.commons.validator.routines.UrlValidator urlValidator =
                    new org.apache.commons.validator.routines.UrlValidator(validSchemes);
            setValid(urlValidator.isValid(buildUrlForValidation()));
        } else {
            setValid(!isRequired());
        }
    }

    private boolean hasValue() {
        return (getComponent().getValue() != null && getComponent().getValue().length() > 0);
    }

    private String buildUrlForValidation() {
        String url = getComponent().getValue();
        for (String scheme : validSchemes) {
            if (url.startsWith(scheme)) {
                return url;
            }
        }

        return DEFAULT_SCHEME + "://" + url;
    }
}