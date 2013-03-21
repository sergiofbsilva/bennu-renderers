package pt.ist.fenixWebFramework.renderers.components;

import org.apache.commons.beanutils.ConvertUtils;

import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

public abstract class HtmlMultipleValueComponent extends HtmlFormComponent {

    public String[] values;

    public HtmlMultipleValueComponent() {
        super();

        values = new String[0];
    }

    public void setValues(String... values) {
        this.values = values;
    }

    @Override
    public String[] getValues() {
        return values;
    }

    @Override
    public String getValue() {
        String[] values = getValues();

        if (values == null) {
            return null;
        }

        if (values.length == 0) {
            return null;
        }

        return values[0];
    }

    @Override
    public Object getConvertedValue() {
        if (hasConverter()) {
            return getConverter().convert(Object.class, getValue());
        }

        return ConvertUtils.convert(getValue(), Object.class);
    }

    @Override
    public Object getConvertedValue(MetaSlot slot) {
        if (hasConverter()) {
            return getConverter().convert(slot.getStaticType(), getValues());
        }

        if (slot.hasConverter()) {
            try {
                return slot.getConverter().newInstance().convert(slot.getStaticType(), getValues());
            } catch (Exception e) {
                throw new RuntimeException("converter specified in meta slot generated an exception", e);
            }
        }

        return ConvertUtils.convert(getValues(), slot.getStaticType());
    }
}