package com.courseselection;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;

@Controller
public class CourseController {


    private final CourseSelectionService courseSelectionService;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public CourseController(
            CourseRepository courseRepository,
            StudentRepository studentRepository) {

        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.courseSelectionService =
                new CourseSelectionService(courseRepository);
    }


    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/student-login")
    public String studentLoginPage() {
        return "student-login";
    }

    @PostMapping("/student-login")
    public String studentLogin(
            @RequestParam String studentId,
            @RequestParam String studentName,
            HttpSession session) {

        Student student = studentRepository.findById(studentId).orElse(null);

        if (student != null) {
            if (!student.getName().equals(studentName.trim())) {
                return "redirect:/student-login?error=name";
            }
        } else {
            student = new Student(studentId, studentName.trim());
            studentRepository.save(student);
        }

        session.setAttribute("student", student);

        return "redirect:/courses";
    }

    @GetMapping("/courses")
    public String courseList(
            @RequestParam(required = false) String keyword,
            Model model,
            HttpSession session) {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/student-login";
        }
        model.addAttribute("student", student);


        List<Course> allCourses = courseRepository.findAll(
                org.springframework.data.domain.Sort.by("id").ascending()
        );


        if (keyword == null || keyword.trim().isEmpty()) {

            model.addAttribute("courses", allCourses);

        } else {

            String searchKeyword = keyword.trim().toLowerCase();

            List<Course> filteredCourses = allCourses.stream()
                    .filter(course ->
                            course.getCourseName().toLowerCase().contains(searchKeyword)
                                    || course.getTeacher().toLowerCase().contains(searchKeyword))
                    .toList();

            model.addAttribute("courses", filteredCourses);
        }

        model.addAttribute("keyword", keyword);

        return "index";
    }

    @PostMapping("/select-course")
    public String selectCourse(
            @RequestParam int courseId,
            HttpSession session) {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/student-login";
        }

        if (student.isPreferencesSubmitted()) {
            return "redirect:/courses";
        }

        Course course = courseRepository.findById(courseId).orElse(null);

        if (course != null) {
            boolean alreadySelected = student.getSelectedCourses()
                    .stream()
                    .anyMatch(c -> c.getId() == courseId);

            if (!alreadySelected) {
                student.selectCourse(course);
                studentRepository.save(student);
            }
        }

        return "redirect:/courses";
    }

    @GetMapping("/my-courses")
    public String myCourses(Model model, HttpSession session) {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/student-login";
        }

        student = studentRepository.findById(student.getStudentId())
                .orElse(null);

        if (student == null) {
            session.removeAttribute("student");
            return "redirect:/student-login";
        }

        session.setAttribute("student", student);


        model.addAttribute("student", student);
        model.addAttribute("selectedCourses", student.getSelectedCourses());
        model.addAttribute("backupCourses", student.getBackupCourses());
        model.addAttribute("allocatedCourses", student.getAllocatedCourses());

        return "my-courses";
    }
    @GetMapping("/schedule")
    public String schedule(Model model, HttpSession session) {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/student-login";
        }

        model.addAttribute("student", student);
        model.addAttribute("selectedCourses", student.getSelectedCourses());

        return "schedule";
    }

    @PostMapping("/drop-course")
    public String dropCourse(
            @RequestParam int courseId,
            HttpSession session) {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/student-login";
        }

        if (student.isPreferencesSubmitted()) {
            return "redirect:/my-courses";
        }

        Course courseToRemove = null;

        for (Course course : student.getSelectedCourses()) {
            if (course.getId() == courseId) {
                courseToRemove = course;
                break;
            }
        }

        if (courseToRemove != null) {
            student.removeCourse(courseToRemove);
            studentRepository.save(student);
        }

        return "redirect:/my-courses";
    }


    @PostMapping("/move-course-up")
    public String moveCourseUp(
            @RequestParam int courseId,
            HttpSession session) {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/student-login";
        }

        if (student.isPreferencesSubmitted()) {
            return "redirect:/my-courses";
        }

        student.moveCourseUp(courseId);
        studentRepository.save(student);

        return "redirect:/my-courses";
    }

    @PostMapping("/move-course-down")
    public String moveCourseDown(
            @RequestParam int courseId,
            HttpSession session) {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/student-login";
        }

        if (student.isPreferencesSubmitted()) {
            return "redirect:/my-courses";
        }

        student.moveCourseDown(courseId);
        studentRepository.save(student);

        return "redirect:/my-courses";
    }

    @PostMapping("/submit-preferences")
    public String submitPreferences(HttpSession session) {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/student-login";
        }

        if (!student.getSelectedCourses().isEmpty()
                && !student.isPreferencesSubmitted()) {

            student.submitPreferences();
            studentRepository.save(student);
        }

        return "redirect:/my-courses";
    }
    @Transactional

    @PostMapping("/allocate-courses")
    public String allocateCourses(HttpSession session) {

        Administrator administrator =
                (Administrator) session.getAttribute("administrator");

        if (administrator == null) {
            return "redirect:/admin-login";
        }

        List<Student> students = studentRepository.findAll();

        for (Student student : students) {

            if (student.isPreferencesSubmitted()
                    && !student.isAllocationCompleted()) {

                courseSelectionService.allocateCourses(student);
                studentRepository.save(student);
            }
        }

        return "redirect:/admin";
    }

    @PostMapping("/add-backup-course")
    public String addBackupCourse(
            @RequestParam int courseId,
            HttpSession session) {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/student-login";
        }

        if (student.isPreferencesSubmitted()) {
            return "redirect:/courses";
        }

        Course course = courseRepository.findById(courseId).orElse(null);

        if (course != null) {
            boolean alreadySelected = student.getSelectedCourses()
                    .stream()
                    .anyMatch(c -> c.getId() == courseId);

            boolean alreadyBackup = student.getBackupCourses()
                    .stream()
                    .anyMatch(c -> c.getId() == courseId);

            if (!alreadySelected && !alreadyBackup) {
                student.addBackupCourse(course);
                studentRepository.save(student);
            }
        }

        return "redirect:/courses";
    }

    @PostMapping("/remove-backup-course")
    public String removeBackupCourse(
            @RequestParam int courseId,
            HttpSession session) {

        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            return "redirect:/student-login";
        }

        if (student.isPreferencesSubmitted()) {
            return "redirect:/my-courses";
        }

        Course courseToRemove = null;

        for (Course course : student.getBackupCourses()) {
            if (course.getId() == courseId) {
                courseToRemove = course;
                break;
            }
        }

        if (courseToRemove != null) {
            student.removeBackupCourse(courseToRemove);
            studentRepository.save(student);
        }

        return "redirect:/my-courses";
    }

    @GetMapping("/admin-login")
    public String adminLoginPage() {
        return "admin-login";
    }

    @PostMapping("/admin-login")
    public String adminLogin(
            @RequestParam String adminId,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        Administrator administrator = MockData.getAdministrator();

        if (administrator.authenticate(adminId, password)) {
            session.setAttribute("administrator", administrator);
            return "redirect:/admin";
        }

        model.addAttribute("error", "管理员账号或密码错误");
        return "admin-login";
    }
    @GetMapping("/admin")
    public String adminPage(Model model, HttpSession session) {

        Administrator administrator =
                (Administrator) session.getAttribute("administrator");

        if (administrator == null) {
            return "redirect:/admin-login";
        }

        model.addAttribute("administrator", administrator);
        model.addAttribute("courses",
                courseRepository.findAll(
                        org.springframework.data.domain.Sort.by("id").ascending()
                ));

        return "admin";
    }

    @GetMapping("/admin/edit-course")
    public String editCourse(
            @RequestParam int courseId,
            Model model,
            HttpSession session) {

        Administrator administrator =
                (Administrator) session.getAttribute("administrator");

        if (administrator == null) {
            return "redirect:/admin-login";
        }

        Course courseToEdit =
                courseRepository.findById(courseId).orElse(null);

        if (courseToEdit == null) {
            return "redirect:/admin";
        }

        model.addAttribute("course", courseToEdit);

        return "edit-course";
    }

    @PostMapping("/admin/update-course")
    public String updateCourse(
            @RequestParam int courseId,
            @RequestParam String teacher,
            @RequestParam int capacity,
            HttpSession session) {

        Administrator administrator =
                (Administrator) session.getAttribute("administrator");

        if (administrator == null) {
            return "redirect:/admin-login";
        }

        Course course = courseRepository.findById(courseId).orElse(null);

        if (course == null) {
            return "redirect:/admin";
        }

        if (capacity <= 0 || capacity < course.getSelectedCount()) {
            return "redirect:/admin/edit-course?courseId="
                    + courseId + "&error=capacity";
        }

        course.setTeacher(teacher);
        course.setCapacity(capacity);

        courseRepository.save(course);

        return "redirect:/admin";
    }
    @GetMapping("/admin/add-course")
    public String addCoursePage(HttpSession session) {

        Administrator administrator =
                (Administrator) session.getAttribute("administrator");

        if (administrator == null) {
            return "redirect:/admin-login";
        }

        return "add-course";
    }
    @PostMapping("/admin/add-course")
    public String addCourse(
            @RequestParam int courseId,
            @RequestParam String courseName,
            @RequestParam String teacher,
            @RequestParam int capacity,
            HttpSession session) {

        Administrator administrator =
                (Administrator) session.getAttribute("administrator");

        if (administrator == null) {
            return "redirect:/admin-login";
        }
        if (courseId <= 0 || capacity <= 0) {
            return "redirect:/admin/add-course";
        }

        if (courseRepository.existsById(courseId)) {
            return "redirect:/admin/add-course?error=duplicate";
        }

        Course newCourse = new Course(
                courseId,
                courseName,
                teacher,
                capacity,
                0
        );

        courseRepository.save(newCourse);

        return "redirect:/admin";
    }
    @PostMapping("/admin/delete-course")
    public String deleteCourse(
            @RequestParam int courseId,
            HttpSession session) {

        Administrator administrator =
                (Administrator) session.getAttribute("administrator");

        if (administrator == null) {
            return "redirect:/admin-login";
        }

        courseRepository.deleteById(courseId);

        return "redirect:/admin";
    }
    @GetMapping("/admin-logout")
    public String adminLogout(HttpSession session) {

        session.removeAttribute("administrator");

        return "redirect:/";
    }
    @GetMapping("/student-logout")
    public String studentLogout(HttpSession session) {
        session.removeAttribute("student");
        return "redirect:/";
    }
    @GetMapping("/api/courses")
    @ResponseBody
    public List<Map<String, Object>> getCoursesApi(
            @RequestParam(required = false) String keyword) {

        String searchKeyword = keyword == null
                ? ""
                : keyword.trim().toLowerCase();

        return courseRepository.findAll().stream()
                .filter(course ->
                        course.getCourseName().toLowerCase().contains(searchKeyword)
                                || course.getTeacher().toLowerCase().contains(searchKeyword))
                .map(course -> Map.<String, Object>of(
                        "id", course.getId(),
                        "courseName", course.getCourseName(),
                        "teacher", course.getTeacher(),
                        "capacity", course.getCapacity(),
                        "selectedCount", course.getSelectedCount()
                ))
                .toList();
    }
}