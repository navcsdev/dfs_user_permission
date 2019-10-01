import app.App;
import org.junit.Test;
import user.Permission;
import user.User;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void createUserWithId() {
        int id = 1;
        User user = new User(id);
        assertNotNull(user.getId());
        assertEquals(user.getId().intValue(), id);
    }

    @Test
    public void testOverridingOfEqual() {
        User u1 = new User(1);
        User u2 = new User(2);
        assertNotEquals(u1, u2);
        assertEquals(u1, new User(1));
    }

    @Test
    public void testOverridingOfHashCode() {
        User u1 = new User(1);
        assertEquals(u1.hashCode(), Objects.hash(u1.getId()));
    }

    @Test
    public void addNewPermissionToUser() {
        final Map<String, Permission> permissionNameMap = new LinkedHashMap<>();
        User manager = new User(1);
        User staff = new User(2);
        staff.setMng(manager);

        Permission permission = Permission.create(permissionNameMap, "1");

        staff.addDftPrm(permission);
        assertNotNull(staff.getDftPrmSet());
        assertTrue(staff.getDftPrmSet().contains(permission));

        // manager should have the permission too
        assertTrue(manager.getAllPrms().contains(permission));
    }

    @Test
    public void removePermissionToUser() {
        final Map<String, Permission> permissionNameMap = new LinkedHashMap<>();
        User manager = new User(1);
        User staff = new User(2);
        staff.setMng(manager);

        Permission permission = Permission.create(permissionNameMap, "1");

        staff.addDftPrm(permission);

        staff.removeDftPrm(Permission.create(permissionNameMap, "1"));
        assertNotNull(staff.getDftPrmSet());
        assertFalse(staff.getDftPrmSet().contains(permission));

        // manager should have the permission too
        assertFalse(manager.getAllPrms().contains(permission));
    }

    @Test
    public void getAllPermissionsOfUser() {
        final Map<String, Permission> permissionNameMap = new LinkedHashMap<>();
        User manager = new User(1);
        User staff = new User(2);
        staff.setMng(manager);

        Permission p1 = Permission.create(permissionNameMap, "1");
        manager.addDftPrm(p1);
        Permission p2 = Permission.create(permissionNameMap, "2");
        staff.addDftPrm(p2);

        Set<Permission> allPrm = manager.getAllPrms();
        assertTrue(allPrm.contains(p1));
        assertTrue(allPrm.contains(p2));
    }

    @Test
    public void testPrintPermission() {
        final Map<String, Permission> permissionNameMap = new LinkedHashMap<>();
        User manager = new User(1);
        User staff = new User(2);
        staff.setMng(manager);

        Permission p1 = Permission.create(permissionNameMap, "C");
        manager.addDftPrm(p1);
        Permission p2 = Permission.create(permissionNameMap, "B");
        staff.addDftPrm(p2);

        Set<Permission> allPrm = manager.getAllPrms();
        assertTrue(allPrm.contains(p1));
        assertTrue(allPrm.contains(p2));

        assertEquals("B,C", manager.printPermission());
    }

    @Test
    public void testLoadData() {
        final Map<String, Permission> permissionNameMap = new LinkedHashMap<>();
        final Map<Integer, User> userMap = User.load(App.getFileHelper("input.txt"), null, permissionNameMap);

        assertTrue(userMap.containsKey(0));
        assertTrue(userMap.containsKey(1));
        assertTrue(userMap.containsKey(2));
        assertTrue(userMap.containsKey(3));
        assertTrue(userMap.containsKey(4));
        assertTrue(userMap.containsKey(5));
        assertTrue(userMap.containsKey(6));

        User ceo = userMap.get(0);
        User u1 = userMap.get(1);
        User u2 = userMap.get(2);
        User u3 = userMap.get(3);
        User u4 = userMap.get(4);
        User u5 = userMap.get(5);
        User u6 = userMap.get(6);

        assertNotNull(ceo);
        assertNotNull(u1);
        assertNotNull(u2);
        assertNotNull(u3);
        assertNotNull(u4);
        assertNotNull(u5);
        assertNotNull(u6);

        assertNull(ceo.getMng());
        assertEquals(ceo, u1.getMng());
        assertEquals(ceo, u2.getMng());
        assertEquals(u1, u3.getMng());
        assertEquals(u1, u4.getMng());
        assertEquals(u1, u5.getMng());
        assertEquals(u2, u6.getMng());

        assertEquals("A,B,C,D,E,F", ceo.printPermission());
        assertEquals("A,B,C,D", u1.printPermission());
        assertEquals("A,B,C,E", u2.printPermission());
        assertEquals("A", u3.printPermission());
        assertEquals("D", u4.printPermission());
        assertEquals("A,C", u5.printPermission());
        assertEquals("A,B", u6.printPermission());
    }

    @Test
    public void testLoadData100000() {
        final Map<String, Permission> permissionNameMap = new LinkedHashMap<>();
        final Map<Integer, User> userMap = User.load(App.getFileHelper("input100000.txt"), null, permissionNameMap);

        assertTrue(userMap.containsKey(0));
        assertTrue(userMap.containsKey(1));
        assertTrue(userMap.containsKey(100000));

        User ceo = userMap.get(0);
        User u1 = userMap.get(1);
        User u99999 = userMap.get(99999);
        User u100000 = userMap.get(100000);

        assertNotNull(ceo);
        assertNotNull(u1);
        assertNotNull(u100000);

        assertNull(ceo.getMng());
        assertEquals(ceo, u1.getMng());
        assertEquals(u99999, u100000.getMng());

        assertTrue(u99999.getAllPrms().contains(permissionNameMap.get("p100")));
        assertEquals(101, ceo.getAllPrms().size());
    }
}
