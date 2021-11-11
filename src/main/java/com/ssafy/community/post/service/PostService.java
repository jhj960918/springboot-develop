package com.ssafy.community.post.service;

import com.ssafy.community.image.service.S3Uploader;
import com.ssafy.community.post.dto.PostRequestDto;
import com.ssafy.community.post.dto.PostResponseDto;
import com.ssafy.community.post.entity.PostEntity;
import com.ssafy.community.post.repository.PostRepository;
import com.ssafy.community.user.entity.UserEntity;
import com.ssafy.community.user.exception.NoUserFoundException;
import com.ssafy.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    public List<PostResponseDto> findAll() {
        List<PostEntity> posts = postRepository.findAll();
        return posts.stream().map(PostResponseDto::from).collect(Collectors.toList());
    }

    public List<PostResponseDto> findByUserId(Long userId) {
        List<PostEntity> posts = postRepository.findByUserId(userId);
        return posts.stream().map(PostResponseDto::from).collect(Collectors.toList());
    }

    public PostResponseDto findById(Long id) {
        PostEntity post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다"));
        return PostResponseDto.from(post);
    }

    public List<String> postS3ImageSave(List<MultipartFile> images) {
        List<String> postImages = new ArrayList<>();
        if (!images.isEmpty()) {
            for (MultipartFile image : images) {
//                images.add(s3Uploader.upload(image, "static"));
                postImages.add(image.getOriginalFilename());
            }
        }
        return postImages;
    }
    @Transactional
    public PostResponseDto save(PostRequestDto postRequestDto) throws IOException {
        UserEntity user = userRepository.findById(postRequestDto.getUserId())
                .orElseThrow(NoUserFoundException::new);
        return PostResponseDto.from(postRepository.save(postRequestDto.toSavePostEntity(user)));
    }
}
