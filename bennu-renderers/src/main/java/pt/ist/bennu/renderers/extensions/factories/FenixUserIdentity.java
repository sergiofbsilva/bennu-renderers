package pt.ist.bennu.renderers.extensions.factories;

import pt.ist.bennu.renderers.core.model.UserIdentity;
import pt.ist.bennu.renderers.security.User;

public class FenixUserIdentity implements UserIdentity {

    private static final long serialVersionUID = 1L;

    private User user;

    public FenixUserIdentity(final User user) {
        super();
        this.user = user;
    }

    public User getUserView() {
        return user;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FenixUserIdentity)) {
            return false;
        }

        final FenixUserIdentity other = (FenixUserIdentity) obj;
        return user == other.user || (user != null && user.equals(other.user));
    }

    @Override
    public int hashCode() {
        return this.user.hashCode();
    }

}
