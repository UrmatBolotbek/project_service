package faang.school.projectservice.validator.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StatusSubsetValidator implements ConstraintValidator<StatusSubset, TaskStatus> {

    private Set<TaskStatus> subset = new HashSet<>();

    @Override
    public void initialize(StatusSubset constraint) {
        subset.addAll(Arrays.asList(constraint.anyOf()));
    }

    @Override
    public boolean isValid(TaskStatus value, ConstraintValidatorContext context) {
        return value == null || subset.contains(value);
    }
}
