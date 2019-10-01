package app;

import user.User;

public class UpdateManagerIhrPrm implements Handler<User> {
    @Override
    public void handle(User user) {
        User mng = user.getMng();
        if (mng != null) mng.addAllIhrPrm(user.getAllPrms());
    }
}
