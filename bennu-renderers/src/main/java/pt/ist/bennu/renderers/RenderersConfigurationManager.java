package pt.ist.bennu.renderers;

import org.apache.struts.action.ExceptionHandler;

import pt.ist.bennu.core.util.ConfigurationManager;

public class RenderersConfigurationManager extends ConfigurationManager {
    /**
     * This <strong>optional</strong> parameter specifies the context path of
     * requests used by the application The default value for this parameter is <code>null</code>.
     */
    protected static String appContext = null;

    /**
     * This <strong>optional</strong> parameter specifies if the
     * fenix-web-framework should generate checksum hashing for each url in
     * order to avoid url tampering
     * 
     */
    protected static Boolean filterRequestWithDigest = false;

    /**
     * This is <strong>required when defining filterRequestWithDigest</strong>
     * parameter and specifies the link to where a URL tampering user shall be
     * sent
     */
    protected static String tamperingRedirect = "";

    // TODO : document this
    protected static String exceptionHandlerClassname = ExceptionHandler.class.getName();

    /**
     * This is a <strong>optional</strong> that allows renderer validators to
     * generate JQuery javascript to also validate user input at client side.
     * Defaults to <strong>false</strong>
     */
    protected static boolean javascriptValidationEnabled = false;

    /**
     * This is a <strong>optional</strong> that determines when the
     * StandardInputRenderer shows the required (*) mark. Defaults to
     * <strong>false</strong>
     */
    protected static boolean requiredMarkShown = false;

    public static String getExceptionHandlerClassname() {
        return getProperty("exceptionHandlerClassname", exceptionHandlerClassname);
    }

    public static String getAppContext() {
        return getProperty("appContext", appContext);
    }

    public static Boolean getFilterRequestWithDigest() {
        return getBooleanProperty("filterRequestWithDigest", filterRequestWithDigest);
    }

    public static String getTamperingRedirect() {
        return getProperty("tamperingRedirect", tamperingRedirect);
    }

    public static boolean isJavascriptValidationEnabled() {
        return getBooleanProperty("javascriptValidationEnabled", javascriptValidationEnabled);
    }

    public static boolean getRequiredMarkShown() {
        return getBooleanProperty("requiredMarkShown", requiredMarkShown);
    }
}
