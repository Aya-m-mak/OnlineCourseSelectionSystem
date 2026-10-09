package com.courseselection;

import java.util.ArrayList;
import java.util.List;

public class MockData {

    private static final List<Course> courses = new ArrayList<>();
    private static final List<Student> students = new ArrayList<>();


    private static final Administrator administrator =
            new Administrator(
                    "admin001",
                    "系统管理员",
                    System.getenv("ADMIN_PASSWORD")
            );


    static {
        courses.add(new Course(
                1, "Java程序设计", "李锐",
                50, 35
        ));

        courses.add(new Course(
                2, "Web应用开发", "李锐",
                40, 28
        ));

        courses.add(new Course(
                3, "数据库原理", "欧广宇",
                45, 32
        ));

        courses.add(new Course(
                4, "计算机网络", "杨桂松",
                50, 41
        ));

        courses.add(new Course(
                5, "操作系统", "袁健",
                40, 36
        ));

        courses.add(new Course(
                6, "数据结构与算法", "曹春萍",
                45, 39
        ));
        courses.get(0).setDayOfWeek("星期一");
        courses.get(0).setClassTime("第3-5节 09:45-11:55");

        courses.get(1).setDayOfWeek("星期三");
        courses.get(1).setClassTime("第8-10节 14:45-16:55");

        courses.get(2).setDayOfWeek("星期二");
        courses.get(2).setClassTime("第3-5节 09:45-11:55");

        courses.get(3).setDayOfWeek("星期四");
        courses.get(3).setClassTime("第6-7节 13:00-14:25");

        courses.get(4).setDayOfWeek("星期五");
        courses.get(4).setClassTime("第1-2节 08:00-09:25");

        courses.get(5).setDayOfWeek("星期一");
        courses.get(5).setClassTime("第8-10节 14:45-16:55");
    }


    public static List<Course> getCourses() {
        return courses;
    }
    public static List<Student> getStudents() {
        return students;
    }
    public static Administrator getAdministrator() {
        return administrator;
    }
}