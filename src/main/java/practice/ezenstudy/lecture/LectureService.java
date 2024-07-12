package practice.ezenstudy.lecture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.ezenstudy.student.Enrollment;
import practice.ezenstudy.teacher.Teacher;
import practice.ezenstudy.teacher.TeacherRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final TeacherRepository teacherRepository;

    public LectureService(LectureRepository lectureRepository, TeacherRepository teacherRepository) {
        this.lectureRepository = lectureRepository;
        this.teacherRepository = teacherRepository;
    }

    public LectureDetailResponse findById(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElse(null);

        if (lecture == null) {
            throw new NoSuchElementException("강의를 찾을 수 없습니다 ID: " + lectureId);
        }

        List<Enrollment> enrollments = lecture.getEnrollments();

        return new LectureDetailResponse(
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getPrice(),
                enrollments.size(),
                enrollments.stream()
                        .map(enrollment -> new LectureDetailResponse.StudentResponse(
                                enrollment.getStudent().getNickname(),
                                enrollment.getCreatedDateTime()))
                        .toList(),
                lecture.getCategory(),
                lecture.getCreatedDatetime(),
                lecture.getModifiedDateTime()
        );
    }

    public List<LectureResponse> findAll(String sort) {
        List<Lecture> lectures = lectureRepository.findAll();

        if (sort != null && sort.equalsIgnoreCase("recent")) {
            lectures = lectureRepository.findAllByOrderByCreatedDateTimeDesc();
        }

        return lectures
                .stream()
                .map(lecture -> new LectureResponse(
                        lecture.getId(),
                        lecture.getTitle(),
                        lecture.getTeacher().getName(),
                        lecture.getPrice(),
                        lecture.getEnrollments().size(),
                        lecture.getCategory(),
                        lecture.getCreatedDatetime()))
                .toList();
    }

    @Transactional
    public void updateById(Long lectureId, UpdateLectureRequest body) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElse(null);

        if (lecture == null) {
            throw new NoSuchElementException("없는 강의 ID: " + lectureId);
        }

        lecture.updateTitleDescriptionPrice(body.title(), body.description(), body.price());
    }

    public void deleteById(Long id) {
//        Lecture lecture = lectureRepository.findById(id)
//                .orElse(null);
//        if (lecture == null) {
//            throw new NoSuchElementException("강의 못 찾음");
//        }

        lectureRepository.deleteById(id);
    }

    public LectureDetailResponse create(CreateLectureRequest request) {
        /*
         * 강의 오브젝트를 만들 때는 강사 ID가 아니라 강사 오브젝트를 전달해야 하기 때문에
         * 강의 등록 요청과 함께 받은 강사 ID로 강사 오브젝트를 찾아야 함
         * */
        Teacher teacher = teacherRepository.findById(request.teacherId())
                .orElse(null);
        if (teacher == null) {
            throw new IllegalArgumentException("잘못된 강사 ID: " + request.teacherId());
        }

        // 강의 등록
        Lecture lecture = lectureRepository.save(
                new Lecture(
                        request.title(),
                        request.description(),
                        teacher,
                        request.price(),
                        request.category()
                )
        );

        // 등록된 강의 데이터를 응답으로 보냄
        return new LectureDetailResponse(
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getPrice(),
                0,
                new ArrayList<>(),
                lecture.getCategory(),
                lecture.getCreatedDatetime(),
                lecture.getModifiedDateTime()
        );
    }
}
