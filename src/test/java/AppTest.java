import app.App;
import app.UpdateManagerIhrPrm;
import org.junit.Test;
import user.Permission;
import user.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void getFileFromResourceFolder() {
        File file = App.getFileHelper("input.txt");
        assertNotNull(file);
    }

    @Test
    public void dfsHelper() {
        Map<Integer, User> userMap = User.load(App.getFileHelper("input.txt"), null, null);

        List<User> users = new ArrayList<>();
        App.postOrderTraversalHelper(userMap.size(), userMap.get(0), users::add);

        assertEquals(7, users.size());
        assertEquals(3, users.get(0).getId().intValue());
        assertEquals(4, users.get(1).getId().intValue());
        assertEquals(5, users.get(2).getId().intValue());
        assertEquals(1, users.get(3).getId().intValue());
        assertEquals(6, users.get(4).getId().intValue());
        assertEquals(2, users.get(5).getId().intValue());
        assertEquals(0, users.get(6).getId().intValue());

        App.postOrderTraversalHelper(userMap.size(), userMap.get(0), new UpdateManagerIhrPrm());
        assertEquals("A,B,C,D,E,F", userMap.get(0).printPermission());
        assertEquals("A,B,C,D", userMap.get(1).printPermission());
        assertEquals("A,B,C,E", userMap.get(2).printPermission());
        assertEquals("A", userMap.get(3).printPermission());
        assertEquals("D", userMap.get(4).printPermission());
        assertEquals("A,C", userMap.get(5).printPermission());
        assertEquals("A,B", userMap.get(6).printPermission());
    }

    @Test
    public void dfsHelper2() {
        final Map<String, Permission> permissionNameMap = new LinkedHashMap<>();
        final Map<Integer, User> userMap = User.load(App.getFileHelper("input2.txt"), null, permissionNameMap);

        List<User> users = new ArrayList<>();
        App.postOrderTraversalHelper(userMap.size(), userMap.get(0), users::add);

        assertEquals(13, users.size());
        assertEquals(3, users.get(0).getId().intValue());
        assertEquals(7, users.get(1).getId().intValue());
        assertEquals(8, users.get(2).getId().intValue());
        assertEquals(9, users.get(3).getId().intValue());
        assertEquals(4, users.get(4).getId().intValue());
        assertEquals(5, users.get(5).getId().intValue());
        assertEquals(1, users.get(6).getId().intValue());
        assertEquals(10, users.get(7).getId().intValue());
        assertEquals(12, users.get(8).getId().intValue());
        assertEquals(11, users.get(9).getId().intValue());
        assertEquals(6, users.get(10).getId().intValue());
        assertEquals(2, users.get(11).getId().intValue());
        assertEquals(0, users.get(12).getId().intValue());
    }

    @Test
    public void handleCommand() {
        final Map<String, Permission> permissionNameMap = new LinkedHashMap<>();
        final List<String> commandList = new LinkedList<>();
        final Map<Integer, User> userMap = User.load(App.getFileHelper("input.txt"), commandList, permissionNameMap);

        Map<Integer, Permission> permissionMap = permissionNameMap.values()
                .stream()
                .collect(Collectors.toMap(Permission::getId, Function.identity()));

        int cmdIdx = 0;
        for (String cmd : commandList) {
            String out = App.handleCommand(cmd, userMap, permissionMap);
            switch (++cmdIdx) {
                case 1:
                    assertNull(out);
                    break;
                case 2:
                    assertEquals("1, 2, 3, 4, 5", out);
                    break;
                case 3:
                    assertNull(out);
                    break;
                case 4:
                    assertEquals("1, 3, 4, 5", out);
                    break;
                case 5:
                    assertEquals(App.UNKNOWN_INPUT, out);
                    break;
            }
        }
    }

    @Test
    public void testInput100000WithCommands() {
        final Map<String, Permission> permissionNameMap = new LinkedHashMap<>();
        final List<String> commandList = new LinkedList<>();
        final Map<Integer, User> userMap = User.load(App.getFileHelper("input100000WithCommands.txt"), commandList, permissionNameMap);
        final Map<Integer, Permission> permissionMap = permissionNameMap.values()
                .stream()
                .collect(Collectors.toMap(Permission::getId, Function.identity()));

        // test add
        String rs = App.handleCommand("ADD 99999 3", userMap, permissionMap);
        assertNull(rs);

        rs = App.handleCommand("QUERY 99999 3", userMap, permissionMap);
        assertEquals("1, 3", rs);
        rs = App.handleCommand("QUERY 1", userMap, permissionMap);
        assertEquals("1, 3", rs);
        rs = App.handleCommand("QUERY 2", userMap, permissionMap);
        assertEquals("1, 3", rs);

        // test remove
        rs = App.handleCommand("REMOVE 99999 3", userMap, permissionMap);
        assertNull(rs);

        rs = App.handleCommand("QUERY 99999 3", userMap, permissionMap);
        assertEquals("1", rs);
        rs = App.handleCommand("QUERY 1", userMap, permissionMap);
        assertEquals("1", rs);
        rs = App.handleCommand("QUERY 2", userMap, permissionMap);
        assertEquals("1", rs);

        // test wrong command
        rs = App.handleCommand("QUErY 2", userMap, permissionMap);
        assertEquals(rs, App.UNKNOWN_INPUT);

        final AtomicInteger idx = new AtomicInteger(0);
        commandList.forEach(cmd -> {
            String actual = App.handleCommand(cmd, userMap, permissionMap);
            switch (idx.incrementAndGet()) {
                case 1:
                    assertNull(actual);
                    break;
                case 2:
                    assertEquals("1, 3", actual);
                    break;
                case 3:
                    assertEquals("1, 3", actual);
                    break;
                case 4:
                    assertEquals("1, 3", actual);
                    break;
                case 5:
                    assertNull(actual);
                    break;
                case 6:
                    assertEquals("1", actual);
                    break;
                case 7:
                    assertEquals("1", actual);
                    break;
                case 8:
                    assertEquals("1", actual);
                    break;
            }
        });
    }
}
