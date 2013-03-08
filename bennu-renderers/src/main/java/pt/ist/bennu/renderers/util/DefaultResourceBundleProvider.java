package pt.ist.bennu.renderers.util;

import java.util.Properties;
import java.util.ResourceBundle;

import pt.ist.bennu.core.i18n.I18N;

public class DefaultResourceBundleProvider extends AbstractMessageResourceProvider {

    public DefaultResourceBundleProvider() {
        super();
    }

    public DefaultResourceBundleProvider(Properties bundleMappings) {
        super(bundleMappings);
    }

    @Override
    public String getMessage(String bundle, String key, String... args) {
        if (containsMapping(bundle)) {
            return format(ResourceBundle.getBundle(getBundleMapping(bundle), I18N.getLocale()).getString(key), args);
        }
        return format(ResourceBundle.getBundle(bundle, I18N.getLocale()).getString(key), args);
    }

}
