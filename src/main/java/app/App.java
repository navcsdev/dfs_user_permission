package app;

import user.Permission;
import user.User;

import java.io.File;
import java.util.*;

public class App {
    public static final String UNKNOWN_INPUT = "UNKNOWN_INPUT";

    public static File getFileHelper(String fileName) {
        String filePath = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(fileName)).getFile();
        return new File(filePath);
    }

    public static void postOrderTraversalHelper(int size, final User user, final Handler<User> handler) {
        final Set<Integer> visited = new LinkedHashSet<>(size);
        final Deque<User> stack = new LinkedList<>();
        stack.push(user);

        while (!stack.isEmpty()) {
            User current = stack.peek();
            LinkedList<User> staffs = current.getStaffs();

            if (!staffs.isEmpty() && !visited.contains(current.getId())) {
                visited.add(current.getId());

                Iterator<User> iterator = staffs.descendingIterator();
                while (iterator.hasNext()) {
                    stack.push(iterator.next());
                }
            } else {
                stack.pop();
                handler.handle(current);
            }
        }
    }

    public static String handleCommand(String command, Map<Integer, User> userMap, Map<Integer, Permission> permissionMap) {
        String[] ss = command.split(" ");
        User user = null;
        Permission permission = null;
        if (ss.length > 1) user = userMap.get(Integer.parseInt(ss[1]));
        if (ss.length > 2) permission = permissionMap.get(Integer.parseInt(ss[2]));

        switch (ss[0]) {
            case "ADD":
                if (user != null && permission != null) {
                    user.addDftPrm(permission);
                }

                return null;

            case "REMOVE":
                if (user != null && permission != null) {
                    user.removeDftPrm(permission);
                }

                return null;

            case "QUERY":
                if (user != null) {
                    return user.printPermissionId();
                }
        }

        return UNKNOWN_INPUT;
    }
}
