package user;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Permission {
    private Integer id;
    private String name;

    private Permission(String name) {
        this.name = name;
    }

    public static Permission create(final Map<String, Permission> permissionNameMap, String name) {
        Permission p = permissionNameMap.get(name);
        if (p != null) return p;

        permissionNameMap.put(name, p = new Permission(name));
        return p;
    }

    public static void fixIds(final Map<String, Permission> permissionNameMap) {
        final AtomicInteger seq = new AtomicInteger(0);
        permissionNameMap.values().stream()
                .map(Permission::getName)
                .sorted()
                .forEach(n -> permissionNameMap.get(n).id = seq.incrementAndGet());
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
