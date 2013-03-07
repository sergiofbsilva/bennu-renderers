package pt.ist.bennu.renderers.extensions.factories;

import java.util.List;

import pt.ist.bennu.renderers.core.model.MetaObject;
import pt.ist.bennu.renderers.core.model.MetaObjectCollection;
import pt.ist.bennu.renderers.services.ServiceManager;
import pt.ist.bennu.renderers.services.ServicePredicate;

public class DomainMetaObjectCollection extends MetaObjectCollection {

    @Override
    public void commit() {
        final List<MetaObject> metaObjects = getAllMetaObjects();
        final ServicePredicate servicePredicate = new ServicePredicate() {
            @Override
            public void execute() {
                for (MetaObject object : metaObjects) {
                    object.commit();
                }
            }
        };
        ServiceManager.execute(servicePredicate);
    }

}
