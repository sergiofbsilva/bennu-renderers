package pt.ist.bennu.renderers.rendererExtensions.factories;

import javax.servlet.http.HttpServletRequest;

import pt.ist.bennu.renderers.core.model.UserIdentity;
import pt.ist.bennu.renderers.core.model.UserIdentityFactory;
import pt.ist.bennu.renderers.security.User;
import pt.ist.bennu.renderers.security.UserView;

public class FenixUserIdentityFactory extends UserIdentityFactory {

    @Override
    public UserIdentity createUserIdentity(HttpServletRequest request) {
        final User user = UserView.getUser();
        return new FenixUserIdentity(user);
    }

}
