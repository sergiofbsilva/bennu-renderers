package pt.ist.bennu.renderers.core;

import pt.ist.bennu.renderers.core.components.HtmlComponent;
import pt.ist.bennu.renderers.core.layouts.Layout;

public class StandardComponentRenderer extends OutputRenderer {

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                return (HtmlComponent) object;
            }

        };
    }

}
