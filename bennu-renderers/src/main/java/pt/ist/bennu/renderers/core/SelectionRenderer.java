package pt.ist.bennu.renderers.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.PropertyUtils;

import pt.ist.bennu.renderers.core.components.converters.ConversionException;
import pt.ist.bennu.renderers.core.components.converters.Converter;
import pt.ist.bennu.renderers.core.model.MetaObject;
import pt.ist.bennu.renderers.core.model.MetaSlot;
import pt.ist.bennu.renderers.core.utils.RenderUtils;

public abstract class SelectionRenderer extends InputRenderer {
    private String providerClass;

    private String from;

    private DataProvider provider;

    private String sortBy;

    public String getProviderClass() {
        return this.providerClass;
    }

    /**
     * The class name of a {@link DataProvider} instance. The provider is
     * responsible for constructing a collection will all possible values.
     * 
     * @property
     */
    public void setProviderClass(String providerClass) {
        this.providerClass = providerClass;
    }

    public String getFrom() {
        return from;
    }

    /**
     * Property with the list of possible options
     * 
     * @param from
     *            {@link Collection} of elements
     */
    public void setFrom(String from) {
        this.from = from;
    }

    public String getSortBy() {
        return this.sortBy;
    }

    /**
     * With this property you can set the criteria used to sort the collection
     * beeing presented. The accepted syntax for the criteria can be seen in
     * {@link RenderUtils#sortCollectionWithCriteria(Collection, String)}.
     * 
     * @property
     */
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    private class FromProvider implements DataProvider {
        @Override
        public Object provide(Object source, Object currentValue) {
            try {
                if (source != null) {
                    return PropertyUtils.getProperty(source, getFrom());
                }
                Class<?> type = getInputContext().getMetaObject().getType();
                String getter = getFrom();
                getter = "get" + getter.substring(0, 1).toUpperCase(Locale.ENGLISH) + getter.substring(1);
                return type.getMethod(getter).invoke(null);
            } catch (Exception e) {
                throw new RuntimeException("exception while accessing 'from' collection", e);
            }
        }

        @Override
        public Converter getConverter() {
            return null;
        }
    }

    protected DataProvider getProvider() {
        if (this.provider == null) {
            String className = getProviderClass();

            try {
                if (providerClass != null) {
                    Class<?> providerCass = Class.forName(className);
                    this.provider = (DataProvider) providerCass.newInstance();
                } else {
                    this.provider = new FromProvider();
                }
            } catch (Exception e) {
                throw new RuntimeException("could not get a data provider instance", e);
            }
        }

        return this.provider;
    }

    protected Converter getConverter() {
        return getProvider() == null ? null : getProvider().getConverter();
    }

    protected Collection<?> getPossibleObjects() {
        Object object = ((MetaSlot) getInputContext().getMetaObject()).getMetaObject().getObject();
        Object value = getInputContext().getMetaObject().getObject();

        Collection<?> collection = (Collection<?>) getProvider().provide(object, value);

        if (getSortBy() == null) {
            return collection;
        }
        return RenderUtils.sortCollectionWithCriteria(collection, getSortBy());
    }

    protected static class SingleSelectOptionConverter extends Converter {

        private final List<MetaObject> metaObjects;
        private final Converter converter;

        public SingleSelectOptionConverter(List<MetaObject> metaObjects, Converter converter) {
            this.metaObjects = metaObjects;
            this.converter = converter;
        }

        @Override
        public Object convert(Class type, Object value) {
            String textValue = (String) value;

            if (textValue == null || textValue.length() == 0) {
                return null;
            }
            if (this.converter != null) {
                return this.converter.convert(type, value);
            }
            for (MetaObject metaObject : this.metaObjects) {
                if (textValue.equals(metaObject.getKey().toString())) {
                    return metaObject.getObject();
                }
            }

            throw new ConversionException("renderers.select.convert.invalid.value");
        }
    }

    protected static class MultipleSelectOptionConverter extends Converter {

        private final List<MetaObject> metaObjects;

        private final Converter converter;

        public MultipleSelectOptionConverter(List<MetaObject> metaObjects, Converter converter) {
            this.metaObjects = metaObjects;
            this.converter = converter;
        }

        @Override
        public Object convert(Class type, Object value) {
            String[] textValues = (String[]) value;

            if (textValues == null || textValues.length == 0) {
                return new ArrayList<Object>();
            }
            if (this.converter != null) {
                return this.converter.convert(type, value);
            }
            List<Object> result = new ArrayList<Object>();

            for (MetaObject metaObject : this.metaObjects) {
                for (String textValue : textValues) {
                    if (textValue.equals(metaObject.getKey().toString())) {
                        result.add(metaObject.getObject());
                    }
                }
            }

            return result;
        }

    }
}
