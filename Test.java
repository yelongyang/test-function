package venus.platform.core.tools;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Test {

    public static void main(String[] args) {
        UnitVo unitVo = UnitService.getUnit("id_unit_01");

        ProjectVo project = ProjectService.getProject("id_project_01");


        List<ProjectVo> projects = ProjectService.getProjects(Arrays.asList("id_project_01", "id_project_02"));
        System.out.println();
    }

    static class UserService {
        private static Map<String, SimpleUserVo> DB = new HashMap<String, SimpleUserVo>() {
            {
                put("id_user_01", new SimpleUserVo("id_user_01", "Jack"));
                put("id_user_02", new SimpleUserVo("id_user_02", "John"));
                put("id_user_03", new SimpleUserVo("id_user_03", "Aaron"));
            }
        };

        public static List<SimpleUserVo> getSimpleUsers(List<String> userIds) {
            List<SimpleUserVo> users = new ArrayList<>(userIds.size());
            for(String userId : userIds) {
                users.add(DB.get(userId));
            }
            return users;
        }

        public static SimpleUserVo getSimpleUser(String userId) {
            return DB.get(userId);
        }


        public static <T> void doSetName(T t, Function<T, String> f, BiConsumer<T, String> c) {
            String userId = BeanUtils.get(f, t);
            SimpleUserVo user = getSimpleUser(userId);
            BeanUtils.set(user, SimpleUserVo::getId, SimpleUserVo::getName, t, f, c);
        }

        public static <T> void doSetName(List<T> l, Function<T, String> f, BiConsumer<T, String> c) {
            List<String> userIds = BeanUtils.distinct(l, f);

            List<SimpleUserVo> users = getSimpleUsers(userIds);

            BeanUtils.set(users, SimpleUserVo::getId, SimpleUserVo::getName, l, f, c);
        }
    }


    static class ProjectService {
        private static Map<String, ProjectVo> DB = new HashMap<String, ProjectVo>() {
            {
                put("id_project_01", new ProjectVo("id_project_01", "projectA", "id_user_01", "id_unit_01"));
                put("id_project_02", new ProjectVo("id_project_02", "projectB", "id_user_02", "id_unit_02"));
                put("id_project_03", new ProjectVo("id_project_03", "projectC", "id_user_03", "id_unit_03"));
            }
        };

        public static ProjectVo getProject(String projectId) {
            ProjectVo project = DB.get(projectId);

            //set creator
            UserService.doSetName(project, ProjectVo::getUserId, ProjectVo::setUserName);

            //set unitName
            UnitService.doSetName(project, ProjectVo::getUnitId, ProjectVo::setUnitName);

            return project;
        }


        public static List<ProjectVo> getProjects(List<String> projectIds) {
            List<ProjectVo> projects = new ArrayList<>(projectIds.size());
            for(String projectId : projectIds) {
                projects.add(DB.get(projectId));
            }

            //set creator
            UserService.doSetName(projects, ProjectVo::getUserId, ProjectVo::setUserName);

            //set unitName
            UnitService.doSetName(projects, ProjectVo::getUnitId, ProjectVo::setUnitName);

            return projects;
        }
    }


    static class UnitService {
        private static Map<String, UnitVo> DB = new HashMap<String, UnitVo>() {
            {
                put("id_unit_01", new UnitVo("id_unit_01", "unit_01", "id_user_01"));
                put("id_unit_02", new UnitVo("id_unit_02", "unit_02", "id_user_02"));
                put("id_unit_03", new UnitVo("id_unit_03", "unit_03", "id_user_03"));
            }
        };

        public static UnitVo getUnit(String unitId) {
            UnitVo unit = DB.get(unitId);

            //set creator
            UserService.doSetName(unit, UnitVo::getCreatedBy, UnitVo::setCreator);

            return unit;
        }

        public static List<UnitVo> getUnits(List<String> unitIds) {
            List<UnitVo> units = new ArrayList<>(unitIds.size());
            for(String unitId : unitIds) {
                units.add(DB.get(unitId));
            }

            //set creator
            UserService.doSetName(units, UnitVo::getCreatedBy, UnitVo::setCreator);
            return units;
        }

        public static SimpleUnitVo getSimpleUnit(String unitId) {
            UnitVo unit = DB.get(unitId);
            if(unit == null) {
                return null;
            }
            return new SimpleUnitVo(unit.getUnitId(), unit.getUnitName());
        }

        public static List<SimpleUnitVo> getSimpleUnits(List<String> unitIds) {
            List<SimpleUnitVo> units = new ArrayList<>(unitIds.size());
            for(String unitId : unitIds) {
                UnitVo unit = DB.get(unitId);
                if(unit == null) {
                    return null;
                }
                units.add(new SimpleUnitVo(unit.getUnitId(), unit.getUnitName()));
            }
            return units;
        }

        public static <T> void doSetName(T t, Function<T, String> f, BiConsumer<T, String> c) {
            String userId = BeanUtils.get(f, t);
            SimpleUnitVo unit = getSimpleUnit(userId);
            BeanUtils.set(unit, SimpleUnitVo::getUnitId, SimpleUnitVo::getUnitName, t, f, c);
        }

        public static <T> void doSetName(List<T> l, Function<T, String> f, BiConsumer<T, String> c) {
            List<String> unitIds = BeanUtils.distinct(l, f);

            List<SimpleUnitVo> units = getSimpleUnits(unitIds);

            BeanUtils.set(units, SimpleUnitVo::getUnitId, SimpleUnitVo::getUnitName, l, f, c);
        }
    }


    @Data
    @AllArgsConstructor
    public static class SimpleUserVo {

        String id;

        String name;
    }

    @Data
    @AllArgsConstructor
    public static class SimpleUnitVo {
        String unitId;
        String unitName;
    }

    @Data
    public static class UnitVo {
        String unitId;
        String unitName;
        String createdBy;
        String creator;

        public UnitVo(String unitId, String unitName, String createdBy) {
            this.unitId = unitId;
            this.unitName = unitName;
            this.createdBy = createdBy;
        }
    }

    @Data
    public static class ProjectVo {

        private String projectId;

        private String projectName;

        private String userId;

        private String userName;

        private String unitId;

        private String unitName;

        public ProjectVo(String projectId, String projectName, String userId, String unitId) {
            this.projectId = projectId;
            this.projectName = projectName;
            this.userId = userId;
            this.unitId = unitId;
        }
    }
}
