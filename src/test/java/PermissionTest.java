import org.junit.Test;
import user.Permission;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

public class PermissionTest {
    @Test
    public void createPermission() {
        final Map<String, Permission> permissionNameMap = new HashMap<>();
        Permission permission = Permission.create(permissionNameMap, "1");
        assertNull(permission.getId());

        assertNotNull(permission.getName());
        assertEquals(permission.getName(), "1");

        Permission.fixIds(permissionNameMap);
        assertNotNull(permission.getId());
        assertEquals(1, permission.getId().intValue());
    }

    @Test
    public void testOverridingOfEqual() {
        Map<String, Permission> permissionNameMap = new HashMap<>();
        Permission p1 = Permission.create(permissionNameMap, "1");
        Permission p2 = Permission.create(permissionNameMap, "2");

        assertNotEquals(p1, p2);
        assertEquals(p1, Permission.create(permissionNameMap, "1"));
    }

    @Test
    public void testOverridingOfHashCode() {
        Map<String, Permission> permissionNameMap = new HashMap<>();
        Permission p1 = Permission.create(permissionNameMap, "1");
        assertEquals(p1.hashCode(), Objects.hash(p1.getName()));
    }
}