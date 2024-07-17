package practice.ezenstudy.lecture;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LectureMapper {
    List<LectureResponse> findAll(LectureSearchParams params);
}