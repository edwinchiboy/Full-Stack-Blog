package com.blog.cutom_blog.models;


import com.blog.cutom_blog.constants.RegistrationStep;
import com.blog.cutom_blog.models.converters.RegistrationStepSetConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "registration")
public class Registration  extends Audit{

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;


    @Column(columnDefinition = "json")
    @Convert(converter = RegistrationStepSetConverter.class)
    private Set<RegistrationStep> completedRegistrationSteps;

    @Builder
    public Registration(
                        final String firstName,
                        final String lastName,
                        final String email,
                        final Set<RegistrationStep> completedRegistrationSteps) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.completedRegistrationSteps = completedRegistrationSteps;
    }

}
