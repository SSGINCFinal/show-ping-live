package com.ssginc.showpinglive.service.implement;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import com.ssginc.showpinglive.dto.object.CreateStreamDto;
import com.ssginc.showpinglive.dto.object.GetStreamRegisterInfoDto;
import com.ssginc.showpinglive.dto.request.RegisterStreamRequestDto;
import com.ssginc.showpinglive.dto.response.GetStreamProductInfoResponseDto;
import com.ssginc.showpinglive.dto.response.GetStreamRegisterInfoResponseDto;
import com.ssginc.showpinglive.dto.response.StartStreamResponseDto;
import com.ssginc.showpinglive.api.StorageLoader;
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
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class StreamServiceImpl implements StreamService {

    @Value("${download.path}")
    private String VIDEO_PATH;

    private final StreamRepository streamRepository;

    private final ProductRepository productRepository;

    private final MemberRepository memberRepository;

    private final StorageLoader storageLoader;

    /**
     * 전체 Vod 목록을 반환해주는 메서드
     * @return vod 목록
     */
    @Override
    public List<StreamResponseDto> getAllVod() {
        return streamRepository.findAllVod();
    }

    /**
     * 페이징 정보가 포함된 Vod 목록을 반환해주는 메서드
     * @param pageable 페이징 정보 객체
     * @return 페이징 정보가 있는 vod 목록
     */
    @Override
    public Page<StreamResponseDto> getAllVodByPage(Pageable pageable) {
        return streamRepository.findAllVodByPage(pageable);
    }

    /**
     * 특정 카테고리의 vod 목록을 반환하는 메서드
     * @param categoryNo 카테고리 번호
     * @return vod 목록
     */
    @Override
    public List<StreamResponseDto> getAllVodByCategory(Long categoryNo) {
        return streamRepository.findAllVodByCategory(categoryNo);
    }

    /**
     * 방송중인 라이브 방송 하나를 반환하는 메서드
     * @return 라이브 방송정보 1개
     */
    @Override
    public StreamResponseDto getLive() {
        List<StreamResponseDto> liveList = streamRepository.findLive();
        return liveList.isEmpty() ? null : liveList.get(0);
    }

    /**
     * 영상번호로 VOD 정보를 가져오는 메서드
     * @param streamNo 영상 번호
     * @return 쿼리를 통해 가져온 영상정보 DTO
     */
    @Override
    public StreamResponseDto getVodByNo(Long streamNo) {
        return streamRepository.findVodByNo(streamNo);
    }

    /**
     * VOD 파일을 NCP에 저장하는 메서드
     * @param title 영상 제목
     * @return VOD 저장 링크
     */
    @Override
    public String uploadVideo(String title) {
        String filePath = VIDEO_PATH + title;
        File file = new File(filePath);
        String fileName = file.getName();
        return storageLoader.uploadFile(file, fileName);
    }

    /**
     * 로그인한 사용자가 등록하고 시작하지 않은 방송 정보를 가져오는 메서드
     * @param memberId
     * @return GetStreamRegisterInfoDto 로그인한 회원으로 등록된 방송 정보
     */
    @Override
    public GetStreamRegisterInfoResponseDto getStreamRegisterInfo(String memberId) {
        try {
            GetStreamRegisterInfoDto streamInfo = streamRepository.findStreamByMemberIdAndStreamStatus(memberId);

            if (streamInfo == null) {
                throw new RuntimeException("해당 회원으로 등록된 방송 정보가 없습니다.");
            }

            NumberFormat nf = NumberFormat.getInstance(Locale.KOREA);
            String formattedPrice = nf.format(streamInfo.getProductPrice()) + "원";

            return GetStreamRegisterInfoResponseDto.builder()
                    .streamNo(streamInfo.getStreamNo())
                    .streamTitle(streamInfo.getStreamTitle())
                    .streamDescription(streamInfo.getStreamDescription())
                    .productNo(streamInfo.getProductNo())
                    .productName(streamInfo.getProductName())
                    .productPrice(formattedPrice)
                    .productSale(streamInfo.getProductSale())
                    .productImg(streamInfo.getProductImg())
                    .build();
        } catch (RuntimeException e) {
            log.error("Exception [Err_Msg]: {}", e.getMessage());
            log.error("Exception [Err_Where]: {}", e.getStackTrace()[0]);

            return null;
        }

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

    /**
     * 방송 시작을 하는 메서드
     * @param streamNo 시작하려는 방송 번호
     * @return 시작한 방송에 대한 정보
     */
    public StartStreamResponseDto startStream(Long streamNo) {
        Stream stream = streamRepository.findById(streamNo).orElseThrow(RuntimeException::new);

        // 방송 시작 시간 설정
        stream.setStreamStartTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        // 방송 상태 송출 중으로 변경
        stream.setStreamStatus(StreamStatus.ONAIR);

        stream = streamRepository.save(stream);

        Product product = stream.getProduct();
        // 천 단위 구분 포맷팅
        NumberFormat nf = NumberFormat.getInstance(Locale.KOREA);
        String formattedPrice = nf.format(product.getProductPrice()) + "원";

        return StartStreamResponseDto.builder()
                .streamTitle(stream.getStreamTitle())
                .streamDescription(stream.getStreamDescription())
                .productImg(product.getProductImg())
                .productNo(product.getProductNo())
                .productName(product.getProductName())
                .productPrice(formattedPrice)
                .productSale(product.getProductSale())
                .build();
    }

    /**
     * 방송 종료를 하는 메서드
     * @param streamNo 종료하려는 방송 번호
     * @return 방송 종료 설정 적용 여부
     */
    public Boolean stopStream(Long streamNo) {
        Stream stream = streamRepository.findById(streamNo).orElseThrow(RuntimeException::new);

        // 방송 종료 시간 설정
        stream.setStreamEndTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        // 방송 상태 송출 종료로 변경
        stream.setStreamStatus(StreamStatus.ENDED);
        // 해당 방송의 상품의 할인율 0으로 변경(할인 종료)
        stream.getProduct().setProductSale(0);

        stream = streamRepository.save(stream);

        if (stream.getStreamEndTime() != null && stream.getStreamStatus() == StreamStatus.ENDED && stream.getProduct().getProductSale() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 시청하려는 방송의 상품을 정보를 가져오는 메서드
     * @param streamNo 시청하려는 방송 번호
     * @return 시청하려는 방송의 상품 정보
     */
    public GetStreamProductInfoResponseDto getStreamProductInfo(Long streamNo){
        Stream stream = streamRepository.findById(streamNo).orElseThrow(RuntimeException::new);
        Product product = stream.getProduct();

        // 상품의 원래 가격
        Long productPrice = product.getProductPrice();
        // 상품에 적용된 할인율
        Integer productSale = product.getProductSale();
        // 상품의 할인된 가격
        Long productSalePrice = productPrice;
        if (productSale != 0) {
            productSalePrice = (long) (productPrice * (1 - (double) productSale / 100));
        }

        // 포맷팅 지정
        NumberFormat nf = NumberFormat.getInstance(Locale.KOREA);

        // 원래 가격 포맷팅 적용
        String formattedPrice = nf.format(productPrice) + "원";
        // 할인 가격 포맷팅 적용
        String formattedSalePrice = nf.format(productSalePrice) + "원";


        return GetStreamProductInfoResponseDto.builder()
                .productNo(product.getProductNo())
                .productImg(product.getProductImg())
                .productName(product.getProductName())
                .productPrice(formattedPrice)
                .productSalePrice(formattedSalePrice)
                .build();
    }

}
