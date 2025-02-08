package com.workintech.spring17challenge.dto;

import com.workintech.spring17challenge.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ApiResponse {
    private Course course;
    private Integer totalGpa;
}
