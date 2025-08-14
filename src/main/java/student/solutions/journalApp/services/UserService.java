package student.solutions.journalApp.services;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import student.solutions.journalApp.entity.Users;
import student.solutions.journalApp.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    //private static final PasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
    @Autowired
    private PasswordEncoder passwordEncoder;
    public boolean saveNewUser(Users users){
        try {
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            users.setRoles(Arrays.asList("USER"));
            userRepository.save(users);
            return true;
        } catch (Exception e){
            log.error("Error occured for: {}", users.getUsername(), e);
            log.debug("hahaha");
            return false;
        }
    }
    public void saveAdmin(Users users){
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setRoles(Arrays.asList("USER","ADMIN"));
        userRepository.save(users);
    }

    public void saveUser(Users users){
        userRepository.save(users);
    }
    public List<Users> getAll(){
        return userRepository.findAll();
    }
    public Optional<Users> findById(ObjectId id){

        return userRepository.findById(id);
    }
    public void deleteById(ObjectId id){
        userRepository.deleteById(id);
    }
    public Users findByUsername(String username){
        return userRepository.findByUsername(username);
    }
}
