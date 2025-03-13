package com.ssginc.showpinglive.service.implement;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssginc.showpinglive.dto.object.CreateStreamDto;
import com.ssginc.showpinglive.dto.object.GetStreamRegisterInfoDto;
import com.ssginc.showpinglive.dto.request.RegisterStreamRequestDto;
import com.ssginc.showpinglive.dto.response.StreamResponseDto;
import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.entity.Product;
import com.ssginc.showpinglive.entity.Stream;
import com.ssginc.showpinglive.entity.StreamStatus;
import com.ssginc.showpinglive.repository.MemberRepository;
import com.ssginc.showpinglive.repository.ProductRepository;
import com.ssginc.showpinglive.repository.StreamRepository;
import com.ssginc.showpinglive.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StreamServiceImpl implements StreamService {

    @Value("${download.path}")
    private String VIDEO_PATH;

    @Value("${ncp.storage.bucketName}")
    private String bucketName;

    private final AmazonS3 amazonS3Client;

    private final StreamRepository streamRepository;

    private final ProductRepository productRepository;

    private final MemberRepository memberRepository;

    @Qualifier("webApplicationContext")
    private final ResourceLoader resourceLoader;

    /**
     * 전체 Vod 목록을 반환해주는 메소드
     * @return vod 목록
     */
    @Override
    public List<StreamResponseDto> getAllVod() {
        return streamRepository.findAllVod();
    }

    /**
     * 페이징 정보가 포함된 Vod 목록을 반환해주는 메소드
     * @param pageable 페이징 정보 객체
     * @return 페이징 정보가 있는 vod 목록
     */
    @Override
    public Page<StreamResponseDto> getAllVodByPage(Pageable pageable) {
        return streamRepository.findAllVodByPage(pageable);
    }

    /**
     * 특정 카테고리의 vod 목록을 반환하는 메소드
     * @param categoryNo 카테고리 번호
     * @return vod 목록
     */
    @Override
    public List<StreamResponseDto> getAllVodByCategory(Long categoryNo) {
        return streamRepository.findAllVodByCategory(categoryNo);
    }

    /**
     * 방송중인 라이브 방송 하나를 반환하는 메소드
     * @return 라이브 방송정보 1개
     */
    @Override
    public StreamResponseDto getLive() {
        List<StreamResponseDto> liveList = streamRepository.findLive();
        return liveList.isEmpty() ? null : liveList.get(0);
    }

    @Override
    public StreamResponseDto getVodByNo(Long streamNo) {
        return streamRepository.findVodByNo(streamNo);
    }

    /**
     * 영상 제목으로 HLS 파일을 받아오는 메소드
     * @param title 영상 제목
     * @return HLS 파일 (확장자: m3u8)
     */
    @Override
    public Mono<Resource> getHLS(String title) {
        return Mono.fromCallable(() -> {
            File inputFile = new File(VIDEO_PATH, title + ".mp4");
            File outputFile = new File(VIDEO_PATH, title + ".m3u8");

            // FFmpeg를 사용하여 HLS로 변환
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i", inputFile.getAbsolutePath(),
                    "-codec:", "copy", "-start_number", "0",
                    "-hls_time", "10", "-hls_list_size", "0",
                    "-f", "hls", outputFile.getAbsolutePath()
            );
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg 변환 실패. Exit code: " + exitCode);
            }

            // 변환된 m3u8 파일을 Resource로 반환
            return resourceLoader.getResource("file:" + outputFile.getAbsolutePath());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 영상 제목과 segment 번호로 TS 파일을 받아오는 메소드
     * @param title 영상 제목
     * @param segment 세그먼트 번호
     * @return TS 파일 (확장자: ts)
     */
    @Override
    public Mono<Resource> getTsSegment(String title, String segment) {
        return Mono.fromCallable(() ->
            resourceLoader.getResource("file:" + VIDEO_PATH + title + segment + ".ts"));
    }

    @Override
    public String uploadVideo(String title) {
        String filePath = VIDEO_PATH + title;
        File file = new File(filePath);
        String fileName = file.getName();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length());

        try (InputStream inputStream = new FileInputStream(file)) {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생", e);
        }

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    /**
     * 로그인한 사용자가 등록하고 시작하지 않은 방송 정보를 가져오는 메서드
     * @param memberId
     * @return GetStreamRegisterInfoDto 로그인한 회원으로 등록된 방송 정보
     */
    @Override
    public GetStreamRegisterInfoDto getStreamRegisterInfo(String memberId) {
        return streamRepository.findStreamByMemberIdAndStreamStatus(memberId);
    }

    /**
     * 방송 데이터를 생성하거나 수정하는 메서드
     * @param memberId
     * @param request
     * @return 생성 혹은 수정된 방송의 방송 번호
     */
    @Override
    public Long createStream(String memberId, RegisterStreamRequestDto request) {
        // 생성 혹은 수정된 streamNo
        Long responseStreamNo;
        Product product = productRepository.findById(request.getProductNo()).orElseThrow(RuntimeException::new);

        // 할인율 전처리
        Integer productSale = request.getProductSale();
        if (productSale == null) {
            productSale = 0;
        }

        Long streamNo = request.getStreamNo();
        // 기존에 등록된 방송 정보가 있는 경우 방송 데이터를 수정
        if (streamNo != null) {
            Stream stream = streamRepository.findById(streamNo).orElseThrow(RuntimeException::new);

            stream.setStreamTitle(request.getStreamTitle());
            stream.setStreamDescription(request.getStreamDescription());
            // 기존에 선택된 상품의 할인율을 0으로 반영
            stream.getProduct().setProductSale(0);
            // 방송 정보를 새로 선택한 상품으로 변경
            stream.setProduct(product);
            // 새로 선택된 상품의 할인율 반영
            product.setProductSale(productSale);

            stream.setStreamEnrollTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")));

            responseStreamNo = streamRepository.save(stream).getStreamNo();
        } else {    // 기존에 등록된 방송 정보가 없는 경우 새로 방송 데이터를 생성
            Member member = memberRepository.findByMemberId(memberId).orElseThrow(RuntimeException::new);

            CreateStreamDto stream = CreateStreamDto.builder()
                    .member(member)
                    .product(product)
                    .streamTitle(request.getStreamTitle())
                    .streamDescription(request.getStreamDescription())
                    .streamStatus(StreamStatus.STANDBY)
                    .streamEnrollTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                    .build();

            // 할인율 적용
            product.setProductSale(productSale);

            responseStreamNo = streamRepository.save(stream.toEntity()).getStreamNo();
        }

        return responseStreamNo;
    }
}
