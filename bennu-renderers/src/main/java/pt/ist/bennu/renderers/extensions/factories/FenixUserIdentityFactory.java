package pt.ist.bennu.renderers.extensions.factories;

import javax.servlet.http.HttpServletRequest;

import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.renderers.core.model.UserIdentity;
import pt.ist.bennu.renderers.core.model.UserIdentityFactory;

public class FenixUserIdentityFactory extends UserIdentityFactory {

    @Override
    public UserIdentity createUserIdentity(HttpServletRequest request) {
        return new FenixUserIdentity(Authenticate.getUser());
    }

}
