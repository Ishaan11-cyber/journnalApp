package student.solutions.journalApp.services;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import student.solutions.journalApp.entity.JournalEntry;
import student.solutions.journalApp.entity.Users;
import student.solutions.journalApp.repository.JournalEntryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryServices {
    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserService userService;
    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username){
        try {
            Users user = userService.findByUsername(username);
            if (user == null) {
                throw new RuntimeException("User not found: " + username);
            }

            // Don't set date here if it's already set in controller
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("An error occurred while saving the entry.", e);
        }
    }
    public void saveEntry(JournalEntry journalEntry){
        journalEntryRepository.save(journalEntry);
    }
    public List<JournalEntry> getAll(){
        return journalEntryRepository.findAll();
    }
    public Optional<JournalEntry> findById(ObjectId id){

        return journalEntryRepository.findById(id);
    }
    @Transactional
    public boolean deleteById(ObjectId id, String username){
        boolean removed=false;
        try{
            Users users=userService.findByUsername(username);
             removed=users.getJournalEntries().removeIf(x->x.getId().equals(id));
            if (removed){
                userService.saveUser(users);
                journalEntryRepository.deleteById(id);
            }
        }
        catch(Exception e){
            System.out.println(e);
            throw new RuntimeException("An error occured while deleting the entry", e);
        }
        return removed;
    }
}

