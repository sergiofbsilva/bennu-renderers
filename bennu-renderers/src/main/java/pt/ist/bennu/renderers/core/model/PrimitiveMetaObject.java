package pt.ist.fenixWebFramework.renderers.model;

public class PrimitiveMetaObject extends SimpleMetaObject {

    public PrimitiveMetaObject(Object object) {
        super(object);
    }

    @Override
    public PrimitiveMetaObjectKey getKey() {
        return new PrimitiveMetaObjectKey(getObject(), getType());
    }
}
