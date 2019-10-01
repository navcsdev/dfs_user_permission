package user;

import app.App;
import app.UpdateManagerIhrPrm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class User {
    /**
     * default permission set
     */
    private Set<Permission> dftPrmSet;
    /**
     * inherit permission set
     */
    private Set<Permission> ihrPrmSet;

    private Integer id;

    /**
     * manager reference
     */
    private User mng;

    /**
     * set of staffs
     */
    private LinkedList<User> staffs;

    public User(Integer id) {
        this.id = id;
        this.dftPrmSet = new LinkedHashSet<>();
        this.ihrPrmSet = new LinkedHashSet<>();
        this.staffs = new LinkedList<>();
    }

    public static Map<Integer, User> load(final File file, final List<String> commandList, Map<String, Permission> permissionNameMap) {
        if (permissionNameMap == null) permissionNameMap = new LinkedHashMap<>();

        final Map<Integer, User> userMap = new LinkedHashMap<>();
        int amtUser = 0;

        long t0 = System.currentTimeMillis();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String st;
            int lineCount = 0;
            int userId = 0;
            while ((st = br.readLine()) != null) {
                lineCount++;

                if (lineCount == 1) {
                    // reading amount of user
                    amtUser = Integer.parseInt(st);
                    continue;
                }

                if (lineCount <= amtUser + 2) {
                    User u = new User(userId++);

                    String prmNames[] = st.split(" ");

                    for (String prmName : prmNames) {
                        u.addDftPrm(Permission.create(permissionNameMap, prmName));
                    }

                    userMap.put(u.getId(), u);
                } else if (lineCount <= (2 * amtUser + 2)) {
                    int managerId = 0;
                    if (!"CEO".equals(st)) {
                        managerId = Integer.parseInt(st);
                    }
                    Integer staffId = lineCount - (amtUser + 2);

                    userMap.get(staffId).setMng(userMap.get(managerId));
                } else {

                    // next line is command
                    if (!"".equals(st) && commandList != null) commandList.add(st);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Permission.fixIds(permissionNameMap);

        System.out.println("---- read input file " + file.getName() + " done in: " + (System.currentTimeMillis() - t0) + " milliseconds");

        t0 = System.currentTimeMillis();
        App.postOrderTraversalHelper(userMap.size(), userMap.get(0), new UpdateManagerIhrPrm());
        System.out.println("---- prepare data structure done in: " + (System.currentTimeMillis() - t0) + " milliseconds");

        return userMap;
    }

    public Set<Permission> getDftPrmSet() {
        return dftPrmSet;
    }

    public Integer getId() {
        return id;
    }

    public User getMng() {
        return mng;
    }

    public void setMng(User user) {
        this.mng = user;

        // update set of staff of manager
        this.mng.staffs.add(this);
    }

    public LinkedList<User> getStaffs() {
        return staffs;
    }

    public Set<Permission> getAllPrms() {
        Set<Permission> allPrm = new LinkedHashSet<>(100);
        allPrm.addAll(this.dftPrmSet);
        allPrm.addAll(this.ihrPrmSet);
        return allPrm;
    }

    public void addDftPrm(Permission p) {
        this.dftPrmSet.add(p);

        User manager = this.getMng();
        while (manager != null) {
            manager.addIhrPrm(p);
            manager = manager.getMng();
        }
    }

    private void addIhrPrm(Permission p) {
        this.ihrPrmSet.add(p);
    }

    public void setDftPrmSet(HashSet<Permission> dftPrmSet) {
        this.dftPrmSet = dftPrmSet;
    }

    public void removeDftPrm(Permission p) {
        this.dftPrmSet.remove(p);

        User manager = this.getMng();
        while (manager != null) {
            manager.removeIhrPrm(p);
            manager = manager.getMng();
        }
    }

    private void removeIhrPrm(Permission p) {
        this.ihrPrmSet.remove(p);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public String printPermission() {
        return this.getAllPrms()
                .stream()
                .map(Permission::getName)
                .sorted()
                .collect(Collectors.joining(","));
    }

    public String printPermissionId() {
        return this.getAllPrms()
                .stream()
                .map(p -> p.getId().toString())
                .sorted()
                .collect(Collectors.joining(", "));
    }

    public void addAllIhrPrm(Set<Permission> prmSet) {
        this.ihrPrmSet.addAll(prmSet);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                '}';
    }
}
