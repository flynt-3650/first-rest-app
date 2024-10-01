package ru.flynt3650.project.first_rest_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flynt3650.project.first_rest_app.models.Person;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
}
