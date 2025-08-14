package student.solutions.journalApp.controllers;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import student.solutions.journalApp.entity.JournalEntry;
import student.solutions.journalApp.entity.Users;
import student.solutions.journalApp.services.JournalEntryServices;
import student.solutions.journalApp.services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/_journal")
public class JournalEntryControllerV2 {
    @Autowired
    private JournalEntryServices journalEntryServices;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        Users user=userService.findByUsername(username);
        List<JournalEntry>all= user.getJournalEntries();
        if(all !=null && !all.isEmpty()){
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping("id/{myId}")
    //ye vali nhi chl rhi hai
    public ResponseEntity<JournalEntry> getJournalEntry(@PathVariable ObjectId myId){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        if (username == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Users user= userService.findByUsername(username);
        List<JournalEntry> collect= user.getJournalEntries().stream().filter(x->x.getId().equals(myId)).collect(Collectors.toList());
        if(!collect.isEmpty()){
            Optional<JournalEntry> journalEntry= journalEntryServices.findById(myId);
            if(journalEntry.isPresent()){
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }
        }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }
    @DeleteMapping("id/{myId}")
    public ResponseEntity<?> deleteJournalEntry(@PathVariable ObjectId myId){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (username == null) {
            return new ResponseEntity<>("Authentication required", HttpStatus.UNAUTHORIZED);
        }
        Users user = userService.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        boolean entryBelongsToUser = user.getJournalEntries().stream()
                .anyMatch(entry -> entry.getId().equals(myId));

        if (!entryBelongsToUser) {
            return new ResponseEntity<>("Journal entry not found or access denied", HttpStatus.NOT_FOUND);
        }
        boolean removed=journalEntryServices.deleteById(myId, username);
        if (removed){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("id/{myId}")
    public ResponseEntity<?> updateJournalEntry(@PathVariable ObjectId myId, @RequestBody JournalEntry newEntry){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user= userService.findByUsername(username);
        List<JournalEntry> collect= user.getJournalEntries().stream().filter(x->x.getId().equals(myId)).collect(Collectors.toList());
        if(!collect.isEmpty()){
            Optional<JournalEntry> journalEntry= journalEntryServices.findById(myId);
            if(journalEntry.isPresent()){
                JournalEntry old=journalEntry.get();

                old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().equals("") ? newEntry.getTitle() : old.getTitle());
                old.setContent(newEntry.getContent() != null && !newEntry.getContent().equals("") ? newEntry.getContent() : old.getContent());

                journalEntryServices.saveEntry(old);

                return new ResponseEntity<>(old, HttpStatus.OK);
            }
        }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry){
        try {
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
            String username= authentication.getName();

            myEntry.setDate(LocalDateTime.now());
            journalEntryServices.saveEntry(myEntry, username);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
