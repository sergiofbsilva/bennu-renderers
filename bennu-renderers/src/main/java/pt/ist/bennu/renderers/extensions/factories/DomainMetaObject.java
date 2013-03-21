package pt.ist.bennu.renderers.extensions.factories;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.beanutils.PropertyUtils;

import pt.ist.bennu.renderers.core.model.CompositeSlotSetter;
import pt.ist.bennu.renderers.core.model.MetaObjectKey;
import pt.ist.bennu.renderers.core.model.MetaSlot;
import pt.ist.bennu.renderers.core.model.SimpleMetaObject;
import pt.ist.bennu.renderers.extensions.util.ObjectChange;
import pt.ist.bennu.renderers.extensions.util.ObjectKey;
import pt.ist.bennu.service.ServiceManager;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.pstm.IllegalWriteException;
import pt.ist.fenixframework.pstm.Transaction;

public class DomainMetaObject extends SimpleMetaObject {

    private long oid;

    private transient DomainObject object;

    protected DomainMetaObject() {
        super(null);
    }

    public DomainMetaObject(final DomainObject object) {
        this();
        setObject(object);
    }

    @Override
    public Object getObject() {
        if (this.object == null) {
            this.object = getPersistentObject();
        }
        return this.object;
    }

    @Override
    protected void setObject(final Object object) {
        this.object = (DomainObject) object;
        if (this.object != null) {
            this.oid = this.object.getOID();
        }
    }

    protected DomainObject getPersistentObject() {
        return Transaction.getObjectForOID(oid);
    }

    public long getOid() {
        return oid;
    }

    protected void setOid(long oid) {
        this.oid = oid;
    }

    @Override
    public Class getType() {
        return getObject().getClass();
    }

    @Override
    public MetaObjectKey getKey() {
        return new MetaObjectKey(getType(), getOid());
    }

    @Override
    public void commit() {
        List<ObjectChange> changes = new ArrayList<ObjectChange>();

        ObjectKey key = new ObjectKey(getOid(), getType());

        for (MetaSlot slot : getAllSlots()) {
            if (slot.isSetterIgnored()) {
                continue;
            }

            if (slot.isCached()) {
                Object change = slot.getObject();
                ObjectChange objectChange = new ObjectChange(key, slot.getName(), change);
                changes.add(objectChange);
            }
        }

        for (CompositeSlotSetter compositeSetter : getCompositeSetters()) {
            try {
                changes.add(new ObjectChange(key, compositeSetter.getSetter(getType()), compositeSetter.getArgumentValues()));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("could not find specialized setter", e);
            }
        }

        callService(changes);
    }

    public static class ServicePredicateWithResult implements Callable<Object> {

        final List<ObjectChange> changes;

        public ServicePredicateWithResult(final List<ObjectChange> changes) {
            this.changes = changes;
        }

        @Override
        public Object call() throws Exception {
            beforeRun(changes);

            Hashtable<ObjectKey, Object> objects = new Hashtable<ObjectKey, Object>();

            for (ObjectChange change : changes) {
                try {
                    Object object = getObject(objects, change);

                    processChange(change, object);
                } catch (InvocationTargetException e) {
                    if (e.getCause() != null && e.getCause() instanceof IllegalWriteException) {
                        throw (IllegalWriteException) e.getCause();
                    }
                    if (e.getCause() != null && e.getCause() instanceof RuntimeException) {
                        throw (RuntimeException) e.getCause();
                    }
                } catch (IllegalAccessException e) {
                    throw new Error(e);
                } catch (NoSuchMethodException e) {
                    throw new Error(e);
                } catch (InstantiationException e) {
                    throw new Error(e);
                }
            }

            afterRun(objects.values());
            return objects.values();
        }

        protected void processChange(ObjectChange change, Object object) throws IllegalAccessException,
                InvocationTargetException, NoSuchMethodException, InstantiationException {
            if (change.slot != null) {
                setProperty(object, change.slot, change.value);
            } else if (change.setter != null) {
                invokeSetter(object, change.setter, change.values);
            }
        }

        private void invokeSetter(Object object, Method setter, Object[] values) {
            try {
                setter.invoke(object, values);
            } catch (InvocationTargetException e) {
                if (e.getCause() != null && e.getCause() instanceof IllegalWriteException) {
                    throw (IllegalWriteException) e.getCause();
                }
                if (e.getCause() != null && e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                } else {
                    throw new RuntimeException("error while invoking specialized setter", e.getTargetException());
                }
            } catch (Exception e) {
                throw new RuntimeException("error while invoking specialized setter", e);
            }
        }

        /**
         * Executed before any change is made to the domain.
         * 
         * @param changes
         *            the list of changes planed for the domain
         */
        protected void beforeRun(List<ObjectChange> changes) {
            // nothing
        }

        /**
         * Executed after all changes are made to the domain. This includes the creation of new objects.
         * 
         * @param touchedObjects
         *            the objects that were edited in the interface or created
         */
        protected void afterRun(Collection<Object> touchedObjects) {
            // nothing
        }

        protected void setProperty(Object object, String slot, Object value) throws IllegalAccessException,
                InvocationTargetException, NoSuchMethodException, InstantiationException {
            Class type = getSlotType(object, slot);

            if (type == null) {
                throw new RuntimeException("could not find type of property " + slot + " in object " + object);
            }

            if (type.isAssignableFrom(Collection.class)) {
                setCollectionProperty(object, slot, (List) value);
            } else {
                try {
                    setSlotProperty(object, slot, value);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("the value '" + value + "' given for slot '" + slot
                            + "' does not match slot's type '" + type + "'");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("could not access to the slot '" + slot + "' of object '" + object
                            + "', probably is not public");
                }
            }
        }

        protected Class getSlotType(Object object, String slot) throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {
            return PropertyUtils.getPropertyType(object, slot);
        }

        protected void setSlotProperty(Object object, String slot, Object value) throws IllegalAccessException,
                InvocationTargetException, NoSuchMethodException {
            PropertyUtils.setProperty(object, slot, value);
        }

        protected void setCollectionProperty(Object object, String slot, List list) throws IllegalAccessException,
                InvocationTargetException, NoSuchMethodException, InstantiationException {
            Collection<?> relation = (Collection<?>) getSlotProperty(object, slot);

            if (relation == null || isWriteableSlot(object, slot)) {
                relation = new ArrayList<>();
                relation.addAll(list);

                // ASSUMPTION: if collection is null then there is a setter that allows the value to be changed
                setSlotProperty(object, slot, relation);
            } else {
                // ASSUMPTION: changing the list affects the relation
                // TODO: cfgi, I hope this is ok but must check
                relation.clear();
                relation.addAll(list);
            }
        }

        protected boolean isWriteableSlot(Object object, String slot) {
            return PropertyUtils.isWriteable(object, slot);
        }

        protected Object getSlotProperty(Object object, String slot) throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException, InstantiationException {
            return PropertyUtils.getProperty(object, slot);
        }

        private Object getObject(Hashtable<ObjectKey, Object> objects, ObjectChange change) {
            Object object = objects.get(change.key);

            if (object == null) {
                object = getNewObject(change);
                objects.put(change.key, object);
            }

            return object;
        }

        protected DomainObject getNewObject(ObjectChange change) {
            return Transaction.getObjectForOID(change.key.getOid());
        }

    }

    protected Callable<Object> getServiceToCall(final List<ObjectChange> changes) {
        return new ServicePredicateWithResult(changes);
    }

    protected Object callService(final List<ObjectChange> changes) {
        final Callable<Object> service = getServiceToCall(changes);
        try {
            return ServiceManager.execute(service);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    protected Object[] getServiceArguments(List<ObjectChange> changes) {
        return new Object[] { changes };
    }

}
