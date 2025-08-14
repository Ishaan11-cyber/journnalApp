package student.solutions.journalApp.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import student.solutions.journalApp.entity.Users;

public interface  UserRepository extends MongoRepository<Users, ObjectId> {
    Users findByUsername(String username);
}
