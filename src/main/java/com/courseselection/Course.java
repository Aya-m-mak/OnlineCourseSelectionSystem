package com.courseselection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")

public class Course {
    @Id
    private int id;
    private String courseName;
    private String teacher;
    private int capacity;
    private int selectedCount;
    private String dayOfWeek;
    private String classTime;

    public Course() {
    }

    public Course(int id, String courseName, String teacher, int capacity, int selectedCount) {
        this.id = id;
        this.courseName = courseName;
        this.teacher = teacher;
        this.capacity = capacity;
        this.selectedCount = selectedCount;
    }

    public int getId() {
        return id;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getTeacher() {
        return teacher;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getSelectedCount() {
        return selectedCount;
    }
    public synchronized boolean hasAvailableSeats() {
        return selectedCount < capacity;
    }

    public synchronized boolean addStudentSafely() {
        if (selectedCount < capacity) {
            selectedCount++;
            return true;
        }
        return false;
    }
    public void removeStudent() {
        if (selectedCount > 0) {
            selectedCount--;
        }
    }
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }
    public boolean hasTimeConflict(Course otherCourse) {

        if (otherCourse == null) {
            return false;
        }

        if (this.dayOfWeek == null || this.classTime == null
                || otherCourse.getDayOfWeek() == null
                || otherCourse.getClassTime() == null) {
            return false;
        }

        if (!this.dayOfWeek.equals(otherCourse.getDayOfWeek())) {
            return false;
        }

        try {
            String firstTime = this.classTime.split(" ")[0];
            String secondTime = otherCourse.getClassTime().split(" ")[0];

            String[] firstParts = firstTime
                    .replace("第", "")
                    .replace("节", "")
                    .split("-");

            String[] secondParts = secondTime
                    .replace("第", "")
                    .replace("节", "")
                    .split("-");

            int firstStart = Integer.parseInt(firstParts[0]);
            int firstEnd = Integer.parseInt(firstParts[firstParts.length - 1]);

            int secondStart = Integer.parseInt(secondParts[0]);
            int secondEnd = Integer.parseInt(secondParts[secondParts.length - 1]);

            return firstStart <= secondEnd
                    && secondStart <= firstEnd;

        } catch (Exception e) {
            return this.classTime.equals(otherCourse.getClassTime());
        }
    }
}