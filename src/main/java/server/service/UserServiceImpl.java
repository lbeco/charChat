package server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceImpl implements UserService {
    private Map<String, User> allUserMap = new ConcurrentHashMap<>();

    {
        allUserMap.put("zhangsan", new User(1,"zhangsan","123"));
        allUserMap.put("lisi", new User(2,"lisi","123"));
    }

    @Override
    public boolean login(String username, String password) {
        String pass = allUserMap.get(username).getPassword();
        if (pass == null) {
            return false;
        }
        return pass.equals(password);
    }
}
