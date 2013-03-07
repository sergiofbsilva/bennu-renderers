package pt.ist.bennu.renderers.core;

import pt.ist.bennu.renderers.core.components.converters.Converter;
import pt.ist.bennu.renderers.core.converters.FloatNumberConverter;

/**
 * {@inheritDoc}
 * 
 * This renderer converts the value to a float with {@link Float#parseFloat(java.lang.String)}.
 * 
 * @author cfgi
 */
public class FloatInputRenderer extends NumberInputRenderer {

    @Override
    protected Converter getConverter() {
        return new FloatNumberConverter();
    }

}
