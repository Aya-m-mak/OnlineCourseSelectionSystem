package com.courseselection;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CourseDataInitializer implements CommandLineRunner {
    private final CourseRepository courseRepository;
    public CourseDataInitializer(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    @Override
    public void run(String... args) {
        if (courseRepository.count() == 0) {
            courseRepository.saveAll(MockData.getCourses());
            System.out.println("Initial courses saved to PostgreSQL.");
        }
    }
}
