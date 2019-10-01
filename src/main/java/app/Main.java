package app;

import user.Permission;
import user.User;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        final List<String> commandList = new LinkedList<>();
        final Map<String, Permission> permissionNameMap = new LinkedHashMap<>();
        final Map<Integer, User> userMap = User.load(App.getFileHelper("input.txt"), commandList, permissionNameMap);

        // print out result for question 1
        try (Writer writer = new BufferedWriter(new FileWriter(new File("./output.txt")))) {
            userMap.values().forEach(user -> {
                        try {
                            writer.write(user.printPermission());
                            writer.write('\n');
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            // handle command as requested in question 4
            Map<Integer, Permission> permissionMap = permissionNameMap.values()
                    .stream()
                    .collect(Collectors.toMap(Permission::getId, Function.identity()));

            for (String cmd : commandList) {
                String out = App.handleCommand(cmd, userMap, permissionMap);
                if (App.UNKNOWN_INPUT.equals(out)) {
                    break;
                }

                if (out != null) {
                    writer.write(out);
                    writer.write('\n');
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
