package pt.ist.fenixWebFramework.rendererExtensions.factories;

import pt.ist.fenixWebFramework.renderers.model.MetaObjectKey;

public class CreationMetaObjectKey extends MetaObjectKey {
    public CreationMetaObjectKey(Class type) {
        super(type, 0);
    }

    @Override
    public String toString() {
        return getType().getName();
    }
}
