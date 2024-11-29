package faang.school.projectservice.validator.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = StatusSubsetValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface StatusSubset {

    TaskStatus[] anyOf();
    String message() default "must be any of {anyOf}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
