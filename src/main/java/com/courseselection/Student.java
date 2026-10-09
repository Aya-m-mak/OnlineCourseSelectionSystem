package com.courseselection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "students")
public class Student {
    @Id
    private String studentId;
    private String name;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "student_selected_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @OrderColumn(name = "priority_position")
    private List<Course> selectedCourses = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "student_backup_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @OrderColumn(name = "backup_position")
    private List<Course> backupCourses = new ArrayList<>();
    private boolean preferencesSubmitted = false;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "student_allocated_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @OrderColumn(name = "allocation_position")
    private List<Course> allocatedCourses = new ArrayList<>();
    private boolean allocationCompleted = false;

    protected Student() {
    }

    public Student(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public List<Course> getSelectedCourses() {
        return selectedCourses;
    }

    public List<Course> getBackupCourses() {
        return backupCourses;
    }

    public void addBackupCourse(Course course) {
        if (!backupCourses.contains(course)
                && !selectedCourses.contains(course)) {
            backupCourses.add(course);
        }
    }

    public void removeBackupCourse(Course course) {
        backupCourses.remove(course);
    }

    public void selectCourse(Course course) {
        if (!selectedCourses.contains(course)) {
            selectedCourses.add(course);
        }
    }
    public void removeCourse(Course course) {
        selectedCourses.remove(course);
    }
    public void moveCourseUp(int courseId) {

        for (int i = 1; i < selectedCourses.size(); i++) {

            if (selectedCourses.get(i).getId() == courseId) {

                Course course = selectedCourses.get(i);

                selectedCourses.set(i, selectedCourses.get(i - 1));
                selectedCourses.set(i - 1, course);

                break;
            }
        }
    }

    public void moveCourseDown(int courseId) {

        for (int i = 0; i < selectedCourses.size() - 1; i++) {

            if (selectedCourses.get(i).getId() == courseId) {

                Course course = selectedCourses.get(i);

                selectedCourses.set(i, selectedCourses.get(i + 1));
                selectedCourses.set(i + 1, course);

                break;
            }
        }
    }
    public boolean isPreferencesSubmitted() {
        return preferencesSubmitted;
    }

    public void submitPreferences() {
        this.preferencesSubmitted = true;
    }
    public List<Course> getAllocatedCourses() {
        return allocatedCourses;
    }

    public boolean isAllocationCompleted() {
        return allocationCompleted;
    }

    public void allocateCourse(Course course) {
        if (!allocatedCourses.contains(course)) {
            allocatedCourses.add(course);
        }
    }

    public void clearAllocatedCourses() {
        allocatedCourses.clear();
    }

    public void completeAllocation() {
        this.allocationCompleted = true;
    }
}
