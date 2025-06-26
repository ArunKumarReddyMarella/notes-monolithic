package com.enotes.monolithic.controller;


import com.enotes.monolithic.dto.FavouriteNoteDto;
import com.enotes.monolithic.dto.NotesDto;
import com.enotes.monolithic.dto.NotesResponse;
import com.enotes.monolithic.entity.FileDetails;
import com.enotes.monolithic.service.NotesService;
import com.enotes.monolithic.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
public class NotesControllerV1 {
    private static final Logger logger = LoggerFactory.getLogger(NotesControllerV1.class);

    @Autowired
    private NotesService notesService;

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> saveNotes(@RequestParam String notes, @RequestParam(required = false) MultipartFile file)
            throws Exception {
        logger.info("Received request to save notes : {}", notes);
        Boolean saveNotes = notesService.saveNotes(notes, file);
        if (saveNotes) {
            return CommonUtil.createBuildResponseMessage("Notes saved success", HttpStatus.CREATED);
        }
        return CommonUtil.createErrorResponseMessage("Notes not saved", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> downloadFile(@PathVariable Integer id) throws Exception {
        logger.info("Received request to download file : {}", id);
        FileDetails fileDetails = notesService.getFileDetails(id);
        byte[] data = notesService.downloadFile(fileDetails);

        HttpHeaders headers = new HttpHeaders();
        String contentType = CommonUtil.getContentType(fileDetails.getOriginalFileName());
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", fileDetails.getOriginalFileName());

        return ResponseEntity.ok().headers(headers).body(data);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllNotes() {
        logger.info("Received request to get all notes");
        List<NotesDto> notes = notesService.getAllNotes();
        if (CollectionUtils.isEmpty(notes)) {
            return ResponseEntity.noContent().build();
        }
        return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
    }

    @GetMapping("/user-notes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAllNotesByUser(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        logger.info("Received request to get all notes by user");
        NotesResponse notes = notesService.getAllNotesByUser(pageNo, pageSize);
        return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteNotes(@PathVariable Integer id) throws Exception {
        logger.info("Received request to delete notes");
        notesService.softDeleteNotes(id);
        return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);
    }

    @GetMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> restoreNotes(@PathVariable Integer id) throws Exception {
        logger.info("Received request to restore deleted notes");
        notesService.restoreNotes(id);
        return CommonUtil.createBuildResponseMessage("Notes restore Success", HttpStatus.OK);
    }

    @GetMapping("/recycle-bin")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    public ResponseEntity<?> getUserRecycleBinNotes() throws Exception {
        logger.info("Received request to get all notes in Recycle Bin");
        List<NotesDto> notes = notesService.getUserRecycleBinNotes();
        if (CollectionUtils.isEmpty(notes)) {
            return CommonUtil.createBuildResponseMessage("Notes not avaible in Recycle Bin", HttpStatus.OK);
        }
        return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> hardDeleteNotes(@PathVariable Integer id) throws Exception {
        logger.info("Received request to hard delete notes");
        notesService.hardDeleteNotes(id);
        return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> emptyUserRecyleBin() throws Exception {
        logger.info("Received request to empty User Recycle Bin");
        notesService.emptyRecycleBin();
        return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);
    }

    @GetMapping("/fav/{noteId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> favoriteNote(@PathVariable Integer noteId) throws Exception {
        logger.info("Received request to add notes:{} in favorite", noteId);
        notesService.favoriteNotes(noteId);
        return CommonUtil.createBuildResponseMessage("Notes added Favorite", HttpStatus.CREATED);
    }

    @DeleteMapping("/un-fav/{favNotId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> unFavoriteNote(@PathVariable Integer favNotId) throws Exception {
        logger.info("Received request to remove notes:{} from favorite", favNotId);
        notesService.unFavoriteNotes(favNotId);
        return CommonUtil.createBuildResponseMessage("Remove Favorite", HttpStatus.OK);
    }

    @GetMapping("/fav-note")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserfavoriteNote() throws Exception {
        logger.info("Received request to get User favorite notes");
        List<FavouriteNoteDto> userFavoriteNotes = notesService.getUserFavoriteNotes();
        if (CollectionUtils.isEmpty(userFavoriteNotes)) {
            return ResponseEntity.noContent().build();
        }
        return CommonUtil.createBuildResponse(userFavoriteNotes, HttpStatus.OK);
    }

    @GetMapping("/copy/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> copyNotes(@PathVariable Integer id) throws Exception {
        logger.info("Received request to copy notes");
        Boolean copyNotes = notesService.copyNotes(id);
        if (copyNotes) {
            return CommonUtil.createBuildResponseMessage("Copied success", HttpStatus.CREATED);
        }
        return CommonUtil.createErrorResponseMessage("Copy failed ! Try Again", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> searchNotes(@RequestParam String keyword, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) throws Exception {
        logger.info("Received request to search notes");
        NotesResponse notes = notesService.getNotesByUserSearchNotes(keyword, pageNo, pageSize);
        return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
    }

}
