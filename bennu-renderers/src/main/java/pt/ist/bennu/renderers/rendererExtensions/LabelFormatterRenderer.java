package pt.ist.fenixWebFramework.rendererExtensions;

import java.util.Properties;

import pt.ist.fenixWebFramework.rendererExtensions.util.RendererMessageResourceProvider;
import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.utl.ist.fenix.tools.resources.LabelFormatter;

public class LabelFormatterRenderer extends OutputRenderer {

    private final Properties bundleMappings;

    public LabelFormatterRenderer() {
        super();

        this.bundleMappings = new Properties();

    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {

                return new HtmlText(((LabelFormatter) object).toString(new RendererMessageResourceProvider(
                        LabelFormatterRenderer.this.bundleMappings)));
            }

        };
    }

    /**
     * 
     * 
     * @property
     */
    public void setBundleName(String bundle, String name) {
        this.bundleMappings.put(bundle, name);
    }

    public String getBundleName(String bundle) {
        return this.bundleMappings.getProperty(bundle);
    }

}
