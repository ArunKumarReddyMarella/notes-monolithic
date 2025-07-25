package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.dto.FavouriteNoteDto;
import com.enotes.monolithic.dto.NotesDto;
import com.enotes.monolithic.dto.NotesResponse;
import com.enotes.monolithic.entity.FavouriteNote;
import com.enotes.monolithic.entity.FileDetails;
import com.enotes.monolithic.entity.Notes;
import com.enotes.monolithic.exception.ExistDataException;
import com.enotes.monolithic.exception.ResourceNotFoundException;
import com.enotes.monolithic.repository.CategoryRepository;
import com.enotes.monolithic.repository.FavouriteNoteRepository;
import com.enotes.monolithic.repository.FileRepository;
import com.enotes.monolithic.repository.NotesRepository;
import com.enotes.monolithic.service.NotesService;
import com.enotes.monolithic.util.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class NotesServiceImpl implements NotesService {
	private static final Logger logger = LoggerFactory.getLogger(NotesServiceImpl.class);

	@Autowired
	private NotesRepository notesRepo;

	@Autowired
	private FavouriteNoteRepository favouriteNoteRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private CategoryRepository categoryRepo;

	@Value("${file.upload.path}")
	private String uploadpath;

	@Autowired
	private FileRepository fileRepo;

	@Autowired
	private S3ServiceImpl s3Service;

	@Override
	public Boolean saveNotes(String notes, MultipartFile file) throws Exception {

		ObjectMapper ob = new ObjectMapper();
		NotesDto notesDto = ob.readValue(notes, NotesDto.class);

		notesDto.setIsDeleted(false);
		notesDto.setDeletedOn(null);

		// update notes if id is given in request
		if (!ObjectUtils.isEmpty(notesDto.getId())) {
			updateNotes(notesDto, file);
		}

		// category validation
		checkCategoryExist(notesDto.getCategory());

		Notes notesMap = mapper.map(notesDto, Notes.class);

		FileDetails fileDtls = saveFileDetails(file);

		if (!ObjectUtils.isEmpty(fileDtls)) {
			notesMap.setFileDetails(fileDtls);
		} else {
			if (ObjectUtils.isEmpty(notesDto.getId())) {
				notesMap.setFileDetails(null);
			}
		}

		Notes saveNotes = notesRepo.save(notesMap);
		if (!ObjectUtils.isEmpty(saveNotes)) {
			return true;
		}
		return false;
	}

	private void updateNotes(NotesDto notesDto, MultipartFile file) throws Exception {

		Notes existNotes = notesRepo.findById(notesDto.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Invalid Notes id"));

		// user not choose any file at update time
		if (ObjectUtils.isEmpty(file)) {
			notesDto.setFileDetails(mapper.map(existNotes.getFileDetails(), NotesDto.FilesDto.class));
		}
		logger.info("update notesDto with file details : {}", notesDto);
	}

	private FileDetails saveFileDetails(MultipartFile file) throws IOException {

		if (!ObjectUtils.isEmpty(file) && !file.isEmpty()) {

			String originalFilename = file.getOriginalFilename();
			String extension = FilenameUtils.getExtension(originalFilename);

			List<String> extensionAllow = Arrays.asList("pdf", "xlsx", "jpg", "png", "docx", "txt");
			if (!extensionAllow.contains(extension)) {
				throw new IllegalArgumentException("invalid file format ! Upload only .pdf , .xlsx,.jpg,.png,.docx,.txt");
			}

			String rndString = UUID.randomUUID().toString();
			String uploadfileName = rndString + "." + extension; // sdfsafbhkljsf.pdf

			String s3FolderPath = "notes/";
			String storePath = s3FolderPath.concat(uploadfileName);
			String fileUrl = s3Service.uploadFile(storePath, file);
			logger.info("file uploaded successfully");
			if (!ObjectUtils.isEmpty(fileUrl)) {
				FileDetails fileDtls = new FileDetails();
				fileDtls.setOriginalFileName(originalFilename);
				fileDtls.setDisplayFileName(getDisplayName(originalFilename));
				fileDtls.setUploadFileName(uploadfileName);
				fileDtls.setFileSize(file.getSize());
				fileDtls.setPath(fileUrl);
				FileDetails saveFileDtls = fileRepo.save(fileDtls);
				logger.info("file saved successfully {}", saveFileDtls);
				return saveFileDtls;
			}
		}
		return null;
	}

	private String getDisplayName(String originalFilename) {
		// java_programming_tutorials.pdf
		// java_prog.pdf
		String extension = FilenameUtils.getExtension(originalFilename);
		String fileName = FilenameUtils.removeExtension(originalFilename);

		if (fileName.length() > 8) {
			fileName = fileName.substring(0, 7);
		}
		fileName = fileName + "." + extension;
		return fileName;
	}

	private void checkCategoryExist(NotesDto.CategoryDto category) throws Exception {
		categoryRepo.findByIdAndIsDeletedFalse(category.getId()).orElseThrow(() -> new ResourceNotFoundException("category id invalid"));
	}

	@Override
	public List<NotesDto> getAllNotes() {
		return notesRepo.findAll().stream().map(note -> mapper.map(note, NotesDto.class)).toList();
	}

	@Override
	public byte[] downloadFile(FileDetails fileDetails) throws Exception {

		return s3Service.downloadFile(fileDetails.getUploadFileName());
	}

	@Override
	public FileDetails getFileDetails(Integer id) throws Exception {
		return fileRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("File is not available"));
	}

	@Override
	public NotesResponse getAllNotesByUser(Integer pageNo, Integer pageSize) {
		Integer userId = CommonUtil.getLoggedInUser().getId();
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Notes> pageNotes = notesRepo.findByCreatedByAndIsDeletedFalse(userId, pageable);

		List<NotesDto> notesDto = pageNotes.get().map(n -> mapper.map(n, NotesDto.class)).toList();

		NotesResponse notes = NotesResponse.builder().notes(notesDto).pageNo(pageNotes.getNumber())
				.pageSize(pageNotes.getSize()).totalElements(pageNotes.getTotalElements())
				.totalPages(pageNotes.getTotalPages()).isFirst(pageNotes.isFirst()).isLast(pageNotes.isLast()).build();

		return notes;
	}

	@Override
	public void softDeleteNotes(Integer id) throws Exception {

		Notes notes = notesRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notes id invalid ! Not Found"));
		notes.setIsDeleted(true);
		notes.setDeletedOn(LocalDateTime.now());
		notesRepo.save(notes);
		logger.warn("Notes deleted successfully of id : {}", id);
	}

	@Override
	public void restoreNotes(Integer id) throws Exception {
		Notes notes = notesRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notes id invalid ! Not Found"));
		notes.setIsDeleted(false);
		notes.setDeletedOn(null);
		notesRepo.save(notes);

	}

	@Override
	public List<NotesDto> getUserRecycleBinNotes() {
		Integer userId = CommonUtil.getLoggedInUser().getId();
		List<Notes> recycleNotes = notesRepo.findByCreatedByAndIsDeletedTrue(userId);
		List<NotesDto> notesDtoList = recycleNotes.stream().map(note -> mapper.map(note, NotesDto.class)).toList();
		return notesDtoList;
	}

	@Override
	public void hardDeleteNotes(Integer id) throws Exception {
		Notes notes = notesRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notes not found"));
		if (notes.getIsDeleted()) {
			deleteFile(notes.getFileDetails().getUploadFileName());
			notesRepo.delete(notes);
			logger.warn("Notes hard deleted successfully of id : {}", id);
		} else {
			throw new IllegalArgumentException("Sorry You cant hard delete Directly");
		}
	}

	private void deleteFile(String path) throws Exception {
		s3Service.deleteFile(path);
	}


	@Override
	public void emptyRecycleBin() {
		Integer userId = CommonUtil.getLoggedInUser().getId();
		List<Notes> recycleNotes = notesRepo.findByCreatedByAndIsDeletedTrue(userId);
		if (!CollectionUtils.isEmpty(recycleNotes)) {
			notesRepo.deleteAllInBatch(recycleNotes);
			logger.warn("Recycle Bin deleted successfully of user id : {}", userId);
		}
	}

	@Override
	public void favoriteNotes(Integer noteId) throws Exception {
		int userId = CommonUtil.getLoggedInUser().getId();
		Notes notes = notesRepo.findById(noteId)
				.orElseThrow(() -> new ResourceNotFoundException("Notes Not found & Id invalid"));
		// if note already favorite
		if (favouriteNoteRepo.findByUserIdAndNoteId(userId, noteId).isPresent()) {
			throw new ExistDataException("Note already favorite");
		}
		FavouriteNote favouriteNote = FavouriteNote.builder().note(notes).userId(userId).build();
		favouriteNoteRepo.save(favouriteNote);
	}

	@Override
	public void unFavoriteNotes(Integer favouriteNoteId) throws Exception {
		FavouriteNote favNote = favouriteNoteRepo.findById(favouriteNoteId)
				.orElseThrow(() -> new ResourceNotFoundException("Favourite Note Not found & Id invalid"));
		favouriteNoteRepo.delete(favNote);
		logger.warn("Favourite Note deleted successfully of id : {}", favouriteNoteId);
	}

	@Override
	public List<FavouriteNoteDto> getUserFavoriteNotes() throws Exception {
		Integer userId = CommonUtil.getLoggedInUser().getId();
		List<FavouriteNote> favouriteNotes = favouriteNoteRepo.findByUserId(userId);
		return favouriteNotes.stream().map(fn -> mapper.map(fn, FavouriteNoteDto.class)).toList();
	}

	@Override
	public Boolean copyNotes(Integer id) throws Exception {
		Notes notes = notesRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notes id invalid ! Not Found"));

		Notes copyNote = Notes.builder().title(notes.getTitle()).description(notes.getDescription())
				.category(notes.getCategory()).isDeleted(false).fileDetails(null).build();
		
		// TODO : Need to check User Validation
		Notes saveCopyNote = notesRepo.save(copyNote);
		if (!ObjectUtils.isEmpty(saveCopyNote)) {
			return true;
		}
		return false;
	}

	@Override
	public NotesResponse getNotesByUserSearchNotes(String keyword, Integer pageNo, Integer pageSize) throws Exception {
		Integer userId = CommonUtil.getLoggedInUser().getId();
		Page<Notes> notes = notesRepo.searchNotes(keyword, userId, PageRequest.of(pageNo, pageSize));

		List<NotesDto> notesDtos = notes.stream().map(n -> mapper.map(n, NotesDto.class)).toList();

		return NotesResponse.builder().notes(notesDtos)
				.pageNo(notes.getNumber()).pageSize(notes.getSize())
				.totalElements(notes.getTotalElements())
				.totalPages(notes.getTotalPages())
				.isFirst(notes.isFirst())
				.isLast(notes.isLast())
				.build();
	}

}
