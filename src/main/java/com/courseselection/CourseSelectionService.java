package com.courseselection;

public class CourseSelectionService {
    private final CourseRepository courseRepository;

    public CourseSelectionService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    public boolean selectCourse(Student student, Course course) {

        if (student.isPreferencesSubmitted()) {
            return false;
        }

        if (student.getSelectedCourses().contains(course)) {
            return false;
        }

        student.selectCourse(course);

        System.out.println("课程已加入志愿列表！");
        System.out.println("学生：" + student.getName());
        System.out.println("学号：" + student.getStudentId());
        System.out.println("课程：" + course.getCourseName());
        System.out.println("教师：" + course.getTeacher());

        return true;
    }
    public void allocateCourses(Student student) {

        if (!student.isPreferencesSubmitted()
                || student.isAllocationCompleted()) {
            return;
        }

        student.clearAllocatedCourses();

        // 按志愿优先级分配主选课程
        for (Course course : student.getSelectedCourses()) {

            boolean hasConflict = false;

            // 检查是否与已分配课程的时间冲突
            for (Course allocatedCourse : student.getAllocatedCourses()) {

                if (course.hasTimeConflict(allocatedCourse)) {
                    hasConflict = true;
                    break;
                }
            }

            // 无时间冲突且有剩余名额时分配课程

            if (!hasConflict) {
                Course lockedCourse = courseRepository
                        .findByIdForUpdate(course.getId())
                        .orElse(null);

                if (lockedCourse != null && lockedCourse.addStudentSafely()) {
                    student.allocateCourse(lockedCourse);
                    courseRepository.save(lockedCourse);
                }
            }
        }

            // 主选课程分配失败时尝试备选课程
        if (student.getAllocatedCourses().isEmpty()) {

            for (Course backupCourse : student.getBackupCourses()) {

                boolean hasConflict = false;

                // 检查备选课程是否与已分配课程冲突
                for (Course allocatedCourse : student.getAllocatedCourses()) {

                    if (backupCourse.hasTimeConflict(allocatedCourse)) {
                        hasConflict = true;
                        break;
                    }
                }

                // 无时间冲突且有剩余名额时分配备选课程

                if (!hasConflict) {
                    Course lockedBackupCourse = courseRepository
                            .findByIdForUpdate(backupCourse.getId())
                            .orElse(null);

                    if (lockedBackupCourse != null) {
                        boolean lockedConflict = false;

                        for (Course allocatedCourse : student.getAllocatedCourses()) {
                            if (lockedBackupCourse.hasTimeConflict(allocatedCourse)) {
                                lockedConflict = true;
                                break;
                            }
                        }

                        if (!lockedConflict && lockedBackupCourse.addStudentSafely()) {
                            student.allocateCourse(lockedBackupCourse);
                            courseRepository.save(lockedBackupCourse);
                            break;
                        }
                    }
                }
            }
        }

                // 完成分配
        student.completeAllocation();
    }
}
