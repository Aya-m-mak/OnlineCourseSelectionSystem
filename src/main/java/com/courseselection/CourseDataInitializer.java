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
        if (!courseRepository.existsById(7)) {
            courseRepository.save(MockData.getCourses().get(6));
        }

        if (!courseRepository.existsById(8)) {
            courseRepository.save(MockData.getCourses().get(7));
        }

        System.out.println("New courses checked.");
    }
}