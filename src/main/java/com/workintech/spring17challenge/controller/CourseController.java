package com.workintech.spring17challenge.controller;

import com.workintech.spring17challenge.dto.ApiResponse;
import com.workintech.spring17challenge.entity.Course;
import com.workintech.spring17challenge.entity.HighCourseGpa;
import com.workintech.spring17challenge.entity.LowCourseGpa;
import com.workintech.spring17challenge.entity.MediumCourseGpa;
import com.workintech.spring17challenge.exceptions.ApiException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/courses")
public class CourseController {

    List<Course> courses;

    private final LowCourseGpa lowCourseGpa;
    private final MediumCourseGpa mediumCourseGpa;
    private final HighCourseGpa highCourseGpa;

    public CourseController(@Qualifier("lowCourseGpa") LowCourseGpa lowCourseGpa,
                            @Qualifier("mediumCourseGpa") MediumCourseGpa mediumCourseGpa,
                            @Qualifier("highCourseGpa") HighCourseGpa highCourseGpa) {
        this.lowCourseGpa = lowCourseGpa;
        this.mediumCourseGpa = mediumCourseGpa;
        this.highCourseGpa = highCourseGpa;
    }

    @PostConstruct
    public void init() {
        courses = new ArrayList<>();
    }



    @GetMapping
    public List<Course> getAll() {
        return this.courses;
    }

    @GetMapping("/{name}")
    public Course getByName(@PathVariable String name) {
        CourseValidation.checkName(name);

        return courses.stream()
                .filter(course -> course.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new ApiException("course not found with name: " + name, HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Course course) {
        CourseValidation.checkCredit(course.getCredit());
        CourseValidation.checkName(course.getName());
        courses.add(course);
        Integer totalGpa = getTotalGpa(course);
        ApiResponse apiResponse = new ApiResponse(course, totalGpa);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody Course course) {
        CourseValidation.checkId(id);
        CourseValidation.checkCredit(course.getCredit());
        CourseValidation.checkName(course.getName());
        course.setId(id);
        Course existingCourse = getExistingCourseById(id);
        int indexOfExisting = courses.indexOf(existingCourse);
        courses.set(indexOfExisting, course);
        Integer totalGpa = getTotalGpa(course);
        ApiResponse apiResponse = new ApiResponse(courses.get(indexOfExisting), totalGpa);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        Course existingCourse = getExistingCourseById(id);
        courses.remove(existingCourse);
    }


    //----------------------------------------------------------------

    public class CourseValidation {
        public static void checkName(String name) {
            if (name == null || name.isEmpty()) {
                throw new ApiException("name cannot be null or empty! " + name, HttpStatus.BAD_REQUEST);
            }
        }

        public static void checkCredit(Integer credit) {
            if (credit == null || credit < 0 || credit > 4) {
                throw new ApiException("credit is null or not between 0-4! " + credit, HttpStatus.BAD_REQUEST);
            }
        }

        public static void checkId(Integer id) {
            if (id == null || id < 0) {
                throw new ApiException("id cannot be null or less then zero! " + id, HttpStatus.BAD_REQUEST);
            }
        }


    }

    private Integer getTotalGpa(Course course) {
        Integer totalGpa = null;
        if (course.getCredit() <= 2) {
            totalGpa = course.getGrade().getCoefficient() * course.getCredit() * lowCourseGpa.getGpa();
        } else if (course.getCredit() == 3) {
            totalGpa = course.getGrade().getCoefficient() * course.getCredit() * mediumCourseGpa.getGpa();
        } else {
            totalGpa = course.getGrade().getCoefficient() * course.getCredit() * highCourseGpa.getGpa();
        }
        return totalGpa;
    }

    private Course getExistingCourseById(Integer id) {
        return courses.stream()
                .filter(cStream -> cStream.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ApiException("course not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}

































